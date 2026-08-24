package com.devaz.minimallauncher.ui

import android.content.Context
import android.content.Intent
import com.devaz.minimallauncher.model.AppInfo

/**
 * Fonctions utilitaires partagées.
 */

/**
 * Lance une application.
 */
fun launchApp(context: Context, appInfo: AppInfo) {
    try {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Lance une application par son package name.
 */
fun launchAppByPackage(context: Context, packageName: String) {
    try {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
