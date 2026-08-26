package com.devaz.minimallauncher.viewmodel

import android.app.AppOpsManager
import android.app.Application
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devaz.minimallauncher.model.AppInfo
import com.devaz.minimallauncher.repository.AppRepository
import com.devaz.minimallauncher.ui.appIconCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AppViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appRepository = AppRepository(application)
    
    private val _apps = MutableLiveData<List<AppInfo>>()
    val apps: LiveData<List<AppInfo>> = _apps
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Cache de toutes les applications
    private var allAppsCache: List<AppInfo> = emptyList()

    /**
     * Charge la liste de toutes les applications.
     * Met en cache la liste complète pour éviter de recharger depuis PackageManager.
     * Pré-charge les icônes en arrière-plan pour une ouverture ultra-fluide.
     * Trie par fréquence d'utilisation (les plus utilisées en premier).
     */
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Si on a déjà les apps en cache, les retourner directement
                if (allAppsCache.isNotEmpty()) {
                    _apps.value = allAppsCache
                    _error.value = null
                    _isLoading.value = false
                    return@launch
                }
                
                // Charger les apps sur Dispatchers.IO pour ne pas bloquer le thread UI
                val appsList = withContext(Dispatchers.IO) {
                    appRepository.getAllApps()
                }
                
                // Trier par fréquence d'utilisation (les plus utilisées en premier)
                allAppsCache = sortAppsByUsage(appsList)
                _apps.value = allAppsCache
                _error.value = if (appsList.isEmpty()) {
                    "Aucune application trouvée ou permission refusée"
                } else {
                    null
                }
                
                // Pré-charger toutes les icônes en arrière-plan en parallèle
                // Cela améliore la fluidité quand on ouvre le tiroir
                if (allAppsCache.isNotEmpty()) {
                    preloadAppIcons(allAppsCache)
                }
                
            } catch (e: SecurityException) {
                _error.value = "Permission QUERY_ALL_PACKAGES refusée. Activez-la dans les paramètres."
                _apps.value = emptyList()
            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
                _apps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Trie les applications par fréquence d'utilisation (plus utilisées en premier).
     * Utilise UsageStatsManager si disponible, sinon trie par ordre alphabétique.
     */
    private suspend fun sortAppsByUsage(apps: List<AppInfo>): List<AppInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val usageStatsManager = getApplication<Application>().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                
                // Vérifier si on a la permission PACKAGE_USAGE_STATS
                val appOps = getApplication<Application>().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    getApplication<Application>().packageName
                )
                
                if (mode == AppOpsManager.MODE_ALLOWED) {
                    // On a la permission, on peut récupérer les stats
                    val timeNow = System.currentTimeMillis()
                    val stats = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        timeNow - TimeUnit.DAYS.toMillis(30),
                        timeNow
                    )
                    
                    // Créer une map packageName -> launchCount
                    val usageCounts = mutableMapOf<String, Int>()
                    stats.forEach { stat ->
                        if (stat.totalTimeInForeground > 0) {
                            usageCounts[stat.packageName] = (usageCounts[stat.packageName] ?: 0) + 1
                        }
                    }
                    
                    // Trier les apps par count décroissant, puis par nom
                    apps.sortedWith(compareByDescending<AppInfo> { 
                        usageCounts[it.packageName] ?: 0 
                    }.thenBy { it.appName.lowercase() })
                } else {
                    // Pas de permission, trier par ordre alphabétique
                    apps.sortedBy { it.appName.lowercase() }
                }
            } catch (e: Exception) {
                // En cas d'erreur, trier par ordre alphabétique
                apps.sortedBy { it.appName.lowercase() }
            }
        }
    }
    
    /**
     * Pré-charge toutes les icônes des applications en parallèle.
     * Utilise async/await pour un chargement optimal.
     */
    private suspend fun preloadAppIcons(apps: List<AppInfo>) {
        // Ne pas bloquer l'UI, lancer sur Dispatchers.IO
        withContext(Dispatchers.IO) {
            // Lancer le pré-chargement de toutes les icônes en parallèle
            val preloadJobs = apps.map { app ->
                async {
                    // Appeler get() sur le cache pour pré-charger l'icône
                    // Le cache gère déjà la déduplication des requêtes
                    appIconCache.get(app.packageName, app.icon)
                }
            }
            
            // Attendre que toutes les icônes soient pré-chargées
            // (mais ne pas bloquer l'UI, c'est déjà sur Dispatchers.IO)
            try {
                awaitAll(*preloadJobs.toTypedArray())
            } catch (e: Exception) {
                // Ignorer les erreurs de pré-chargement d'icônes individuelles
                // Elles seront chargées à la demande si nécessaire
            }
        }
    }

    /**
     * Recherche des applications par nom.
     * Utilise le cache local au lieu de recharger depuis le repository.
     */
    fun searchApps(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Filtrer localement depuis le cache
                val filteredApps = if (query.isBlank()) {
                    allAppsCache
                } else {
                    allAppsCache.filter {
                        it.appName.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
                    }
                }
                _apps.value = filteredApps
                _error.value = if (filteredApps.isEmpty()) {
                    "Aucune application correspondante"
                } else {
                    null
                }
            } catch (e: Exception) {
                _error.value = "Erreur : ${e.message}"
                _apps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Réinitialise le cache et recharge les applications.
     * Utile après un changement de permission ou pour forcer un rafraîchissement.
     */
    fun refreshApps() {
        allAppsCache = emptyList()
        loadApps()
    }
}
