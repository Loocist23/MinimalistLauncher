package com.devaz.minimallauncher.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devaz.minimallauncher.viewmodel.AppViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Écran d'accueil avec heure et applications favorites.
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: AppViewModel = viewModel()
    
    // Formatage de l'heure
    val currentTime = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    
    // Date
    val currentDate = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
    }
    
    // State pour stocker les modifications des apps favorites
    val favoriteAppsState = remember { mutableStateOf(
        listOf(
            FavoriteApp("Téléphone", Icons.Default.Call, "com.android.phone"),
            FavoriteApp("Caméra", Icons.Default.Camera, "com.android.camera"),
            FavoriteApp("Play Store", Icons.Default.PlayArrow, "com.android.vending"),
            FavoriteApp("YouTube", Icons.Default.Videocam, "com.google.android.youtube")
        )
    ) }
    
    // Fonction pour remplacer une app à un emplacement spécifique
    fun replaceAppAtPosition(position: Int, newApp: FavoriteApp?) {
        if (newApp == null) {
            // Supprimer l'app à cette position
            favoriteAppsState.value = favoriteAppsState.value.toMutableList().apply { removeAt(position) }
        } else {
            // Remplacer l'app à cette position
            favoriteAppsState.value = favoriteAppsState.value.toMutableList().apply {
                this[position] = newApp
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
   ) {
        // En-tête avec heure et date
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Grille des applications favorites
        FavoriteAppsGrid(favoriteAppsState.value, onAppChange = ::replaceAppAtPosition)
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Indicateur pour le geste de swipe vers le haut
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "↑ Scroll vers le haut pour les apps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

/**
 * Grille des applications favorites.
 */
@Composable
fun FavoriteAppsGrid(currentApps: List<FavoriteApp>, onAppChange: (position: Int, newApp: FavoriteApp?) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            currentApps.forEachIndexed { index, app ->
                FavoriteAppItem(
                    app = app,
                    position = index,
                    onAppChange = onAppChange
                )
            }
        }
    }
}

/**
 * Modèle pour une application favorite.
 */
data class FavoriteApp(
    val name: String,
    val icon: ImageVector,
    val packageName: String
)

/**
 * Élément d'application favorite.
 */
@Composable
fun FavoriteAppItem(app: FavoriteApp, position: Int, onAppChange: (position: Int, newApp: FavoriteApp?) -> Unit) {
    val context = LocalContext.current
    
    val showMenuState = remember { mutableStateOf(false) }
    val showAppInfoDialogState = remember { mutableStateOf(false) }
    
    // Gestes pour l'interaction avec l'app
    Column(
        modifier = Modifier
            .pointerInput(app) {
                detectTapGestures(
                    onTap = {
                        // Tap rapide pour afficher les infos
                        showAppInfoDialogState.value = true
                    },
                    onLongPress = {
                        // Long press pour ouvrir le menu de personnalisation
                        showMenuState.value = true
                    }
                )
            }
            .clickable {
                // Click pour lancer l'app (priorité)
                com.devaz.minimallauncher.ui.launchAppByPackage(context, app.packageName)
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icône de l'application
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = app.icon,
                contentDescription = app.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Nom de l'application
        Text(
            text = app.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        // Menu de personnalisation (apparaît sur long press)
        if (showMenuState.value) {
            AppCustomizationMenu(
                position = position,
                currentApp = app,
                allApps = getAllAvailableApps(context),
                onDismiss = { showMenuState.value = false },
                onAppSelect = { newApp ->
                    onAppChange(position, newApp)
                    showMenuState.value = false
                }
            )
        }
        
        // Dialog pour afficher les infos de l'app
        if (showAppInfoDialogState.value) {
            AppInfoDialog(
                app = app,
                onDismiss = { showAppInfoDialogState.value = false }
            )
        }
    }
}

/**
 * Obtient la liste de toutes les applications disponibles sur le device.
 */
private fun getAllAvailableApps(context: Context): List<FavoriteApp> {
    val packageManager = context.packageManager
    val apps = mutableListOf<FavoriteApp>()
    
    try {
        val packages = packageManager.getInstalledPackages(0)
        packages.forEach { packageInfo ->
            val appName = packageInfo.applicationInfo?.loadLabel(packageManager)?.toString() ?: packageInfo.packageName
            val icon = packageInfo.applicationInfo?.loadIcon(packageManager)
            
            // On exclut les apps système importantes
            val excludedPackages = listOf(
                "com.android.phone",
                "com.android.dialer",
                "com.android.settings",
                "com.android.systemui",
                "com.google.android.googlequicksearchbox"
            )
            
            if (!excludedPackages.any { packageInfo.packageName.contains(it, ignoreCase = true) }) {
                apps.add(FavoriteApp(appName, Icons.Default.Apps, packageInfo.packageName))
            }
        }
    } catch (e: Exception) {
        // En cas d'erreur, on retourne une liste vide
    }
    
    return apps
}

/**
 * Menu de personnalisation pour modifier une application à un emplacement spécifique.
 */
@Composable
fun AppCustomizationMenu(
    position: Int,
    currentApp: FavoriteApp,
    allApps: List<FavoriteApp>,
    onDismiss: () -> Unit,
    onAppSelect: (FavoriteApp?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Personnaliser l'emplacement ${position + 1}") },
        text = {
            Column {
                Text("Choisissez l'application à cet emplacement :")
                Spacer(modifier = Modifier.height(8.dp))
                
                // Liste des applications disponibles
                allApps.forEach { app ->
                    val isSelected = app.packageName == currentApp.packageName
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onAppSelect(app) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = app.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Sélectionné",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Option pour supprimer l'application de cet emplacement
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onAppSelect(null) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Supprimer cet emplacement")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

/**
 * Dialog pour afficher les informations d'une application.
 */
@Composable
fun AppInfoDialog(app: FavoriteApp, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Informations") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = app.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Package: ${app.packageName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}


