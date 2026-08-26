package com.devaz.minimallauncher.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devaz.minimallauncher.model.AppInfo
import com.devaz.minimallauncher.viewmodel.AppViewModel
import kotlinx.coroutines.launch

/**
 * Tiroir d'applications avec index alphabétique.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppDrawer(
    onClose: () -> Unit,
    onScrollStarted: () -> Unit = {},
    onScrollStopped: () -> Unit = {},
    isAnimating: Boolean = false,
    swipeThreshold: Float = 50f
) {
    val context = LocalContext.current
    val viewModel: AppViewModel = viewModel()
    val apps by viewModel.apps.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(true)
    
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Tracker pour détecter si on est en train de scroller
    // Utiliser derivedStateOf pour éviter les recompositions inutiles
    val isScrolling = remember { mutableStateOf(false) }
    
    // Mettre à jour les callbacks parent avec un délai pour éviter les appels trop fréquents
    LaunchedEffect(isScrolling.value) {
        if (isScrolling.value) {
            onScrollStarted()
        } else {
            // Petit délai pour s'assurer que le scroll est vraiment terminé
            kotlinx.coroutines.delay(100)
            if (!isScrolling.value) {
                onScrollStopped()
            }
        }
    }
    
    // Filtrer et trier les apps - utiliser derivedStateOf pour optimiser
    val filteredApps by remember(apps, searchQuery) {
        derivedStateOf {
            val filtered = if (searchQuery.isBlank()) {
                apps
            } else {
                apps.filter {
                    it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
                }
            }
            filtered.sortedBy { it.appName.uppercase() }
        }
    }
    
    // Pré-calculer le groupement par lettre pour éviter de le faire dans le content de LazyColumn
    val groupedApps by remember(filteredApps) {
        derivedStateOf {
            filteredApps.groupBy { 
                it.appName.uppercase().firstOrNull()?.toString() ?: "#" 
            }.toSortedMap(String.CASE_INSENSITIVE_ORDER)
        }
    }
    
    // Les apps sont déjà chargées dans AppContent au démarrage du launcher
    // Connection pour détecter le scroll
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: androidx.compose.ui.geometry.Offset, source: NestedScrollSource): androidx.compose.ui.geometry.Offset {
                if (available.y != 0f) {
                    isScrolling.value = true
                }
                return androidx.compose.ui.geometry.Offset.Zero
            }
            
            override fun onPostScroll(consumed: androidx.compose.ui.geometry.Offset, available: androidx.compose.ui.geometry.Offset, source: NestedScrollSource): androidx.compose.ui.geometry.Offset {
                // Si on a consommé du scroll vertical, on est en train de scroller
                if (consumed.y != 0f) {
                    isScrolling.value = true
                } else if (available.y == 0f) {
                    // Si plus de scroll disponible, on a arrêté
                    isScrolling.value = false
                }
                return androidx.compose.ui.geometry.Offset.Zero
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
            .nestedScroll(nestedScrollConnection)
            .pointerInput(isAnimating) {
                if (isAnimating) {
                    // Bloquer tous les clics pendant l'animation
                    detectTapGestures {}
                }
            }
    ) {
        // En-tête avec barre de recherche et bouton fermer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { 
                    if (!isAnimating) {
                        searchQuery = it
                        viewModel.searchApps(it)
                    }
                },
                onSearch = { 
                    if (!isAnimating) {
                        viewModel.searchApps(searchQuery)
                    }
                },
                active = false,
                onActiveChange = {},
                enabled = !isAnimating,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            ) {
                // Contenu de la SearchBar
            }
            
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fermer",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable(
                        enabled = !isAnimating,
                        onClick = onClose
                    )
                    .size(36.dp)
            )
        }
        
        // Contenu principal avec liste et index
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Liste des applications
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (filteredApps.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune application trouvée. Activez la permission QUERY_ALL_PACKAGES.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Utiliser le groupement pré-calculé
                        groupedApps.forEach { (letter, appsInGroup) ->
                            item {
                                // En-tête de la lettre
                                Text(
                                    text = letter,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                )
                            }
                            
                            items(appsInGroup, key = { it.packageName }) { appInfo ->
                                AppDrawerItem(
                                    appInfo = appInfo,
                                    onClick = { com.devaz.minimallauncher.ui.launchApp(context, appInfo) }
                                )
                            }
                        }
                    }
                }
                
                // Scroll to top bouton
                if (filteredApps.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Retour en haut",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    coroutineScope.launch {
                                        listState.scrollToItem(0)
                                    }
                                }
                                .size(36.dp)
                        )
                    }
                }
            }
            
            // Index alphabétique à droite
            AlphabetIndex(
                groupedApps = groupedApps,
                listState = listState,
                onLetterSelected = { letter ->
                    coroutineScope.launch {
                        // Trouver la première app de cette lettre
                        val appsInGroup = groupedApps[letter]
                        if (!appsInGroup.isNullOrEmpty()) {
                            // Trouver l'index global de la première app de ce groupe
                            val firstApp = appsInGroup.first()
                            val globalIndex = filteredApps.indexOf(firstApp)
                            if (globalIndex >= 0) {
                                listState.scrollToItem(globalIndex)
                            }
                        }
                    }
                }
            )
        }
    }
}

/**
 * Index alphabétique vertical sur le côté droit.
 */
@Composable
fun AlphabetIndex(
    groupedApps: Map<String, List<AppInfo>>,
    listState: LazyListState,
    onLetterSelected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val hoveredLetter = remember { mutableStateOf<String?>(null) }
    
    // Obtenir les lettres uniques triées depuis groupedApps
    val letters = remember(groupedApps) {
        val lettersSet = groupedApps.keys.toMutableSet()
        lettersSet.add("#")
        lettersSet.sortedWith(String.CASE_INSENSITIVE_ORDER)
    }
    
    Column(
        modifier = Modifier
            .width(40.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(80.dp)) // Espace pour la barre de recherche
        
        letters.forEach { letter ->
            val isHovered = hoveredLetter.value == letter
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clickable(onClick = { onLetterSelected(letter) })
                    .background(
                        if (isHovered) 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else 
                            Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = if (isHovered) androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp) 
                                else androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp)
                    ),
                    color = if (isHovered) 
                        MaterialTheme.colorScheme.primary
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Élément d'application dans le tiroir.
 */
@Composable
fun AppDrawerItem(appInfo: AppInfo, onClick: () -> Unit) {
    // Utiliser remember pour stabiliser la référence
    val stableAppInfo = remember(appInfo) { appInfo }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                drawable = stableAppInfo.icon, 
                appName = stableAppInfo.appName, 
                packageName = stableAppInfo.packageName
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Nom et package
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stableAppInfo.appName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Package name masqué mais toujours utilisé pour la recherche
            // Text(
            //     text = stableAppInfo.packageName,
            //     style = MaterialTheme.typography.bodySmall,
            //     color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            //     maxLines = 1,
            //     overflow = TextOverflow.Ellipsis
            // )
        }
    }
}
