package com.devaz.minimallauncher.repository

import android.content.Context
import android.content.pm.PackageManager
import com.devaz.minimallauncher.model.AppInfo

/**
 * Repository pour gérer la récupération des applications installées.
 */
class AppRepository(private val context: Context) {
    
    private val packageManager: PackageManager by lazy {
        context.packageManager
    }

    /**
     * Récupère la liste de toutes les applications installées.
     * Requiert la permission QUERY_ALL_PACKAGES.
     */
    fun getAllApps(): List<AppInfo> {
        return try {
            val mainIntent = android.content.Intent(android.content.Intent.ACTION_MAIN, null)
            mainIntent.addCategory(android.content.Intent.CATEGORY_LAUNCHER)
            
            val packages = packageManager.queryIntentActivities(mainIntent, 0)
            
            packages.map { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                val appName = resolveInfo.loadLabel(packageManager).toString()
                val icon = resolveInfo.loadIcon(packageManager)
                val isSystemApp = isSystemPackage(packageName)
                
                AppInfo(
                    packageName = packageName,
                    appName = appName,
                    icon = icon,
                    isSystemApp = isSystemApp
                )
            }.sortedBy { it.appName.lowercase() }
            
        } catch (e: SecurityException) {
            // Permission QUERY_ALL_PACKAGES non accordée
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Vérifie si un package est une application système.
     */
    private fun isSystemPackage(packageName: String): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            (packageInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Filtre les applications par nom.
     */
    fun searchApps(query: String): List<AppInfo> {
        return getAllApps().filter {
            it.appName.contains(query, ignoreCase = true) ||
            it.packageName.contains(query, ignoreCase = true)
        }
    }

    /**
     * Récupère l'intent pour lancer une application.
     */
    fun getLaunchIntent(packageName: String): android.content.Intent? {
        return try {
            packageManager.getLaunchIntentForPackage(packageName)
        } catch (e: Exception) {
            null
        }
    }
}
