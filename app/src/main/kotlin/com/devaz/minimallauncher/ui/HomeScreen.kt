package com.devaz.minimallauncher.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Groups
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
import com.devaz.minimallauncher.model.AppInfo
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
    // Chaque position a un type spécifique avec son propre filtre
    val favoriteAppsState = remember { mutableStateOf(
        listOf(
            FavoriteApp("Téléphone", Icons.Default.Call, "com.android.phone"),      // Position 0: Appels
            FavoriteApp("Discord", Icons.Default.Groups, "com.discord"),            // Position 1: Réseaux sociaux
            FavoriteApp("Play Store", Icons.Default.PlayArrow, "com.android.vending"), // Position 2: Stores
            FavoriteApp("YouTube", Icons.Default.Videocam, "com.google.android.youtube") // Position 3: Vidéos
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
    
    val viewModel: AppViewModel = viewModel()
    val allApps by viewModel.apps.observeAsState(emptyList())
    
    val showMenuState = remember { mutableStateOf(false) }
    val showModifyDialogState = remember { mutableStateOf(false) }
    val showAppInfoDialogState = remember { mutableStateOf(false) }
    
    // Gestes pour l'interaction avec l'app
    Box(
        modifier = Modifier
            .wrapContentSize()
            .pointerInput(app) {
                detectTapGestures(
                    onTap = {
                        // Tap pour lancer l'app
                        com.devaz.minimallauncher.ui.launchAppByPackage(context, app.packageName)
                    },
                    onLongPress = {
                        // Long press pour ouvrir le menu
                        showMenuState.value = true
                    }
                )
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Contenu de l'item d'app
        Column(
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
        }
        
        // Nouveau menu simplifié (apparaît sur long press)
        if (showMenuState.value) {
            AppCustomizationSimpleMenu(
                currentApp = app,
                onDismiss = { showMenuState.value = false },
                onInfoClick = { 
                    showMenuState.value = false
                    showAppInfoDialogState.value = true
                },
                onModifyClick = {
                    showMenuState.value = false
                    showModifyDialogState.value = true
                }
            )
        }
        
        // Dialog pour modifier l'app (liste des apps vidéo)
        if (showModifyDialogState.value) {
            AppModifyDialog(
                currentApp = app,
                allApps = allApps,
                position = position,
                onDismiss = { showModifyDialogState.value = false },
                onAppSelect = { newApp ->
                    onAppChange(position, newApp)
                    showModifyDialogState.value = false
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
 * Menu simplifié avec options : Infos + Modifier.
 */
@Composable
fun AppCustomizationSimpleMenu(
    currentApp: FavoriteApp,
    onDismiss: () -> Unit,
    onInfoClick: () -> Unit,
    onModifyClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Options pour ${currentApp.name}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bouton Infos
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onInfoClick()
                        onDismiss()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Informations")
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Bouton Modifier
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onModifyClick()
                        onDismiss()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Modifier l'application")
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

/**
 * Filtre les apps par catégorie selon la position
 */
private fun getFilteredApps(allApps: List<AppInfo>, position: Int): List<AppInfo> {
    return when (position) {
        0 -> getCallApps(allApps)      // Position 0: Apps d'appel
        1 -> getSocialApps(allApps)    // Position 1: Réseaux sociaux
        2 -> getStoreApps(allApps)     // Position 2: Stores d'apps
        3 -> getVideoApps(allApps)     // Position 3: Apps vidéo
        else -> allApps                 // Par défaut: toutes les apps
    }
}

/**
 * Filtre pour les apps d'appel (téléphone, dialer, contacts)
 */
private fun getCallApps(allApps: List<AppInfo>): List<AppInfo> {
    val callKeywords = listOf("phone", "téléphone", "dialer", "contacts", "appel", "call", "whatsapp")
    return allApps.filter { appInfo ->
        val nameLower = appInfo.appName.lowercase()
        val packageLower = appInfo.packageName.lowercase()
        callKeywords.any { keyword ->
            nameLower.contains(keyword) || packageLower.contains(keyword)
        }
    }
}

/**
 * Filtre pour les apps de réseaux sociaux
 */
private fun getSocialApps(allApps: List<AppInfo>): List<AppInfo> {
    val socialKeywords = listOf(
        "discord", "whatsapp", "messenger", "facebook", "instagram", "tiktok",
        "telegram", "signal", "snapchat", "twitter", "x", "linkedin", "reddit"
    )
    return allApps.filter { appInfo ->
        val nameLower = appInfo.appName.lowercase()
        val packageLower = appInfo.packageName.lowercase()
        socialKeywords.any { keyword ->
            nameLower.contains(keyword) || packageLower.contains(keyword)
        }
    }
}

/**
 * Filtre pour les stores d'applications
 */
private fun getStoreApps(allApps: List<AppInfo>): List<AppInfo> {
    val storeKeywords = listOf(
        "play store", "store", "vending", "f-droid", "aurora", "galaxy store",
        "app store", "market", "aptoide"
    )
    return allApps.filter { appInfo ->
        val nameLower = appInfo.appName.lowercase()
        val packageLower = appInfo.packageName.lowercase()
        storeKeywords.any { keyword ->
            nameLower.contains(keyword) || packageLower.contains(keyword)
        }
    }
}

/**
 * Filtre pour les apps vidéo (YouTube, etc.)
 */
private fun getVideoApps(allApps: List<AppInfo>): List<AppInfo> {
    val videoKeywords = listOf("youtube", "yt ", "ytube", "video", "vimeo", "dailymotion", "twitch", "prime video")
    val excludedKeywords = listOf("instagram", "tiktok", "insta", "tik tok", "reels", "shorts", "facebook", "meta")
    
    return allApps.filter { appInfo ->
        val nameLower = appInfo.appName.lowercase()
        val packageLower = appInfo.packageName.lowercase()
        
        val isVideoApp = videoKeywords.any { keyword ->
            nameLower.contains(keyword) || packageLower.contains(keyword)
        }
        
        val isExcluded = excludedKeywords.any { keyword ->
            nameLower.contains(keyword) || packageLower.contains(keyword)
        }
        
        isVideoApp && !isExcluded
    }
}

/**
 * Dialog pour choisir une app pour remplacer l'app actuelle.
 * La liste est filtrée selon la position (0: Appels, 1: Réseaux sociaux, 2: Stores, 3: Vidéos)
 */
@Composable
fun AppModifyDialog(
    currentApp: FavoriteApp,
    allApps: List<AppInfo>,
    position: Int,
    onDismiss: () -> Unit,
    onAppSelect: (FavoriteApp) -> Unit
) {
    val categoryName = when (position) {
        0 -> "Appels"
        1 -> "Réseaux sociaux"
        2 -> "Stores"
        3 -> "Vidéos"
        else -> "Applications"
    }
    
    val filteredApps = remember(allApps, position) { getFilteredApps(allApps, position) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choisir une application $categoryName") },
        text = {
            if (filteredApps.isEmpty()) {
                Text("Aucune application $categoryName trouvée.")
            } else {
                Column {
                    Text("Sélectionnez une app pour remplacer ${currentApp.name} :")
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Liste des apps filtrées avec scroll
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { appInfo ->
                            val isSelected = appInfo.packageName == currentApp.packageName
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        // Utiliser une icône par défaut selon la catégorie
                                        val defaultIcon = when (position) {
                                            0 -> Icons.Default.Call
                                            1 -> Icons.Default.Groups
                                            2 -> Icons.Default.PlayArrow
                                            3 -> Icons.Default.Videocam
                                            else -> Icons.Default.Apps
                                        }
                                        onAppSelect(FavoriteApp(appInfo.appName, defaultIcon, appInfo.packageName))
                                        onDismiss()
                                    }
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AppIcon(
                                        drawable = appInfo.icon,
                                        appName = appInfo.appName,
                                        packageName = appInfo.packageName
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = appInfo.appName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(8.dp))
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
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
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


