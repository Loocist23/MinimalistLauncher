package com.devaz.minimallauncher.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devaz.minimallauncher.model.AppInfo
import com.devaz.minimallauncher.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LauncherScreen() {
    val context = LocalContext.current
    val viewModel: AppViewModel = viewModel()
    val apps by viewModel.apps.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(true)
    val error by viewModel.error.observeAsState()
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Charger les apps au démarrage
    LaunchedEffect(Unit) {
        viewModel.loadApps()
    }
    
    // Afficher les erreurs via Snackbar
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Barre de recherche
        SearchBar(
            query = searchQuery,
            onQueryChange = { 
                searchQuery = it
                viewModel.searchApps(it)
            },
            onSearch = { viewModel.searchApps(searchQuery) },
            active = false,
            onActiveChange = {},
            placeholder = { Text("Rechercher une application...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Rechercher")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {}

        // Contenu principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (apps.isEmpty()) {
                Text(
                    text = "Aucune application trouvée. Vérifiez la permission QUERY_ALL_PACKAGES.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                // Liste des applications
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(apps) { appInfo ->
                        AppItem(
                            appInfo = appInfo,
                            onClick = { launchApp(context, appInfo) }
                        )
                    }
                }
            }
            
            // Snackbar pour afficher les erreurs
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun AppItem(appInfo: AppInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône de l'application
        Box(
            modifier = Modifier
                .size(48.dp)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(drawable = appInfo.icon, appName = appInfo.appName)
        }

        Spacer(modifier = Modifier.size(16.dp))

        // Nom de l'application
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = appInfo.appName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = appInfo.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Composant pour afficher l'icône d'une application.
 * Gère la conversion du Drawable en ImageBitmap.
 */
@Composable
fun AppIcon(drawable: Drawable?, appName: String) {
    val bitmap = remember(drawable) {
        try {
            // Convertir Drawable en Bitmap
            // Essayer d'abord comme BitmapDrawable
            (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap?.asImageBitmap()
                // Sinon essayer de créer un bitmap à partir du drawable
                ?: run {
                    val bitmap = android.graphics.Bitmap.createBitmap(
                        drawable?.intrinsicWidth ?: 48,
                        drawable?.intrinsicHeight ?: 48,
                        android.graphics.Bitmap.Config.ARGB_8888
                    )
                    val canvas = android.graphics.Canvas(bitmap)
                    drawable?.setBounds(0, 0, canvas.width, canvas.height)
                    drawable?.draw(canvas)
                    bitmap.asImageBitmap()
                }
        } catch (e: Exception) {
            null
        }
    }
    
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = appName,
            modifier = Modifier.size(36.dp)
        )
    } else {
        // Fallback si drawable est null ou conversion échouée
        // Utiliser une icône Material comme fallback
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = appName,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
        )
    }
}


