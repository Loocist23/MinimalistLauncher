package com.devaz.minimallauncher.repository

import android.content.Context
import android.content.SharedPreferences

/**
 * Repository pour gérer les paramètres du launcher via SharedPreferences.
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE)

    // Thème : "system", "light", "dark"
    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    // Couleur d'accent : clé parmi les couleurs prédéfinies
    var accentColorKey: String
        get() = prefs.getString(KEY_ACCENT_COLOR, "purple") ?: "purple"
        set(value) = prefs.edit().putString(KEY_ACCENT_COLOR, value).apply()

    // Taille de la police de l'horloge en sp
    var clockFontSize: Int
        get() = prefs.getInt(KEY_CLOCK_FONT_SIZE, 72)
        set(value) = prefs.edit().putInt(KEY_CLOCK_FONT_SIZE, value.coerceIn(40, 120)).apply()

    // Nombre d'apps favorites (4, 6, 8)
    var favoriteAppsCount: Int
        get() = prefs.getInt(KEY_FAV_APPS_COUNT, 4)
        set(value) = prefs.edit().putInt(KEY_FAV_APPS_COUNT, value).apply()

    // Activer/désactiver la recherche de contacts
    var contactsSearchEnabled: Boolean
        get() = prefs.getBoolean(KEY_CONTACTS_SEARCH, true)
        set(value) = prefs.edit().putBoolean(KEY_CONTACTS_SEARCH, value).apply()

    // Apps favorites personnalisées (sérialisées en JSON simple : "name|package;name|package;...")
    var favoriteAppsSerialized: String
        get() = prefs.getString(KEY_FAV_APPS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_FAV_APPS, value).apply()

    fun resetFavorites() {
        prefs.edit().remove(KEY_FAV_APPS).apply()
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ACCENT_COLOR = "accent_color"
        private const val KEY_CLOCK_FONT_SIZE = "clock_font_size"
        private const val KEY_FAV_APPS_COUNT = "fav_apps_count"
        private const val KEY_CONTACTS_SEARCH = "contacts_search"
        private const val KEY_FAV_APPS = "fav_apps"
    }
}
