package com.devaz.minimallauncher.model

import android.graphics.drawable.Drawable

/**
 * Modèle représentant une application installée sur l'appareil.
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String? = null,
    val versionCode: Int? = null,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false
) : Comparable<AppInfo> {
    // Tri alphabétique par nom d'application
    override fun compareTo(other: AppInfo): Int {
        return this.appName.compareTo(other.appName, ignoreCase = true)
    }
}
