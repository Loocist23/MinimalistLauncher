package com.devaz.minimallauncher.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Écran affiché quand la permission QUERY_ALL_PACKAGES n'est pas accordée.
 */
@Composable
fun PermissionScreen(onPermissionChecked: () -> Unit) {
    val context = LocalContext.current
    val checkCount = remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icône d'avertissement
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.height(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Titre
        Text(
            text = "Permission requise",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Description
        Text(
            text = "Pour afficher la liste des applications installées, " +
                   "MinimalLauncher a besoin de la permission pour consulter toutes les applications.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Instructions
        Text(
            text = "Cette permission doit être activée manuellement dans les paramètres Android.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Bouton principal pour ouvrir les paramètres
        Button(
            onClick = {
                try {
                    // ACTION_MANAGE_ALL_APPS_PERMISSIONS est disponible depuis API 30 (Android 11)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent("android.settings.MANAGE_ALL_APPS_PERMISSIONS")
                        context.startActivity(intent)
                    } else {
                        // Fallback pour les anciennes versions
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent)
                    }
                } catch (e: Exception) {
                    // Si l'intent échoue, ouvrir les paramètres de l'application
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ouvrir les paramètres"
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text("Ouvrir les paramètres")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bouton pour réessayer
        OutlinedButton(
            onClick = {
                checkCount.value++
                onPermissionChecked()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Réessayer"
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text("J'ai activé la permission, réessayer")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Instructions détaillées
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Instructions :",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "1. Dans les paramètres, trouvez MinimalLauncher\n" +
                       "2. Allez dans Permissions ou Autorisations\n" +
                       "3. Activez \"Afficher les applications\" ou \"QUERY_ALL_PACKAGES\"\n" +
                       "4. Revenez ici et cliquez sur \"Réessayer\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
