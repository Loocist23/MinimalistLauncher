package com.devaz.minimallauncher.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
fun HomeScreen(
    onDrawerOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        FavoriteAppsGrid()
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Bouton pour ouvrir le tiroir
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = "Toutes les applications",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = onDrawerOpen)
            )
        }
    }
}

/**
 * Grille des applications favorites.
 */
@Composable
fun FavoriteAppsGrid() {
    // Applications favorites avec leurs icônes et packages
    val favoriteApps = listOf(
        FavoriteApp("Téléphone", Icons.Default.Call, "com.android.phone"),
        FavoriteApp("Caméra", Icons.Default.Camera, "com.android.camera"),
        FavoriteApp("Play Store", Icons.Default.PlayArrow, "com.android.vending"),
        FavoriteApp("YouTube", Icons.Default.Videocam, "com.google.android.youtube")
    )
    
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
            favoriteApps.forEach { app ->
                FavoriteAppItem(app = app)
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
fun FavoriteAppItem(app: FavoriteApp) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .clickable {
                com.devaz.minimallauncher.ui.launchAppByPackage(context, app.packageName)
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Text(
            text = app.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}


