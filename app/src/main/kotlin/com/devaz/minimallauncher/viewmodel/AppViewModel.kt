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

    /**
     * Charge la liste de toutes les applications.
     */
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val appsList = appRepository.getAllApps()
                _apps.value = appsList.sorted()
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
     */
    fun searchApps(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val filteredApps = if (query.isBlank()) {
                    appRepository.getAllApps()
                } else {
                    appRepository.searchApps(query)
                }
                _apps.value = filteredApps.sorted()
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
}
