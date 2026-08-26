package com.devaz.minimallauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devaz.minimallauncher.model.AppInfo
import com.devaz.minimallauncher.repository.AppRepository
import kotlinx.coroutines.launch

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
                
                val appsList = appRepository.getAllApps()
                allAppsCache = appsList.sorted()
                _apps.value = allAppsCache
                _error.value = if (appsList.isEmpty()) {
                    "Aucune application trouvée ou permission refusée"
                } else {
                    null
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
