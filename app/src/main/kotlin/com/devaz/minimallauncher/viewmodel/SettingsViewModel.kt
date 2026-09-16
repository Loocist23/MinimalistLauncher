package com.devaz.minimallauncher.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devaz.minimallauncher.repository.SettingsRepository

/**
 * ViewModel pour les paramètres du launcher.
 * Expose les settings en LiveData et permet de les modifier.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)

    private val _themeMode = MutableLiveData(repo.themeMode)
    val themeMode: LiveData<String> = _themeMode

    private val _accentColorKey = MutableLiveData(repo.accentColorKey)
    val accentColorKey: LiveData<String> = _accentColorKey

    private val _clockFontSize = MutableLiveData(repo.clockFontSize)
    val clockFontSize: LiveData<Int> = _clockFontSize

    private val _favoriteAppsCount = MutableLiveData(repo.favoriteAppsCount)
    val favoriteAppsCount: LiveData<Int> = _favoriteAppsCount

    private val _contactsSearchEnabled = MutableLiveData(repo.contactsSearchEnabled)
    val contactsSearchEnabled: LiveData<Boolean> = _contactsSearchEnabled

    fun setThemeMode(value: String) {
        repo.themeMode = value
        _themeMode.value = value
    }

    fun setAccentColorKey(value: String) {
        repo.accentColorKey = value
        _accentColorKey.value = value
    }

    fun setClockFontSize(value: Int) {
        repo.clockFontSize = value
        _clockFontSize.value = value
    }

    fun setFavoriteAppsCount(value: Int) {
        repo.favoriteAppsCount = value
        _favoriteAppsCount.value = value
    }

    fun setContactsSearchEnabled(value: Boolean) {
        repo.contactsSearchEnabled = value
        _contactsSearchEnabled.value = value
    }

    fun resetFavorites() {
        repo.resetFavorites()
    }

    fun getFavoriteAppsSerialized(): String = repo.favoriteAppsSerialized

    fun setFavoriteAppsSerialized(value: String) {
        repo.favoriteAppsSerialized = value
    }
}
