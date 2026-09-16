package com.devaz.minimallauncher.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Velocity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devaz.minimallauncher.model.AppInfo
import com.devaz.minimallauncher.model.ContactInfo
import com.devaz.minimallauncher.viewmodel.AppViewModel
import com.devaz.minimallauncher.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

/**
 * Tiroir d'applications avec recherche, contacts et index alphabétique.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppDrawer(
    onClose: () -> Unit,
    onOpen: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    isAnimating: Boolean = false,
    swipeThreshold: Float = 50f
) {
    val context = LocalContext.current
    val viewModel: AppViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val apps by viewModel.apps.observeAsState(emptyList())
    val contacts by viewModel.contacts.observeAsState(emptyList())
    val contactsSearchEnabled by settingsViewModel.contactsSearchEnabled.observeAsState(true)
    val isLoading by viewModel.isLoading.observeAsState(true)

    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Réinitialiser la recherche et le scroll quand on ferme le tiroir
    fun handleClose() {
        searchQuery = ""
        coroutineScope.launch { listState.scrollToItem(0) }
        onClose()
    }

    var dragTriggered by remember { mutableStateOf(false) }
    var accumulatedDrag by remember { mutableStateOf(0f) }

    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    // Connection nested scroll pour détecter le pull-to-close
    val currentIsAnimating = rememberUpdatedState(isAnimating)
    val currentHandleClose = rememberUpdatedState<() -> Unit> { handleClose() }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y > 0f && isAtTop && !currentIsAnimating.value && !dragTriggered) {
                    accumulatedDrag += available.y
                    if (accumulatedDrag > 80f) {
                        dragTriggered = true
                        currentHandleClose.value()
                    }
                    return available
                }
                if (available.y < 0f) {
                    accumulatedDrag = 0f
                    dragTriggered = false
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                accumulatedDrag = 0f
                dragTriggered = false
                return Velocity.Zero
            }
        }
    }

    val isSearching = searchQuery.isNotBlank()

    // Filtrer les apps
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

    // Filtrer les contacts (uniquement pendant la recherche)
    val filteredContacts by remember(contacts, searchQuery, contactsSearchEnabled) {
        derivedStateOf {
            if (!contactsSearchEnabled || searchQuery.isBlank()) {
                emptyList()
            } else {
                contacts.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    // Grouper les apps par lettre
    val groupedApps by remember(filteredApps) {
        derivedStateOf {
            filteredApps.groupBy {
                it.appName.uppercase().firstOrNull()?.toString() ?: "#"
            }.toSortedMap(String.CASE_INSENSITIVE_ORDER)
        }
    }

    // Pré-calculer l'index de chaque lettre dans le LazyColumn
    val letterToItemIndex = remember(groupedApps, isSearching, filteredContacts) {
        var index = 0
        val map = linkedMapOf<String, Int>()

        // Entrée "Paramètres" en tête quand on ne recherche pas
        if (!isSearching) {
            index += 1
        }

        if (isSearching && filteredContacts.isNotEmpty()) {
            index += 1 + filteredContacts.size
        }
        if (isSearching && filteredApps.isNotEmpty()) {
            index += 1
        }

        groupedApps.forEach { (letter, appsInGroup) ->
            map[letter] = index
            index += 1 + appsInGroup.size
        }
        map
    }

    // Animation de visibilité de l'index alphabétique
    val indexAlpha = remember { Animatable(0f) }

    LaunchedEffect(listState.isScrollInProgress, isSearching) {
        if (!isSearching) {
            if (listState.isScrollInProgress) {
                indexAlpha.animateTo(1f, animationSpec = tween(200))
            } else {
                indexAlpha.animateTo(0f, animationSpec = tween(400))
            }
        } else {
            indexAlpha.animateTo(0f, animationSpec = tween(200))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
            .pointerInput(isAnimating, isAtTop) {
                if (!isAnimating) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            dragTriggered = false
                        },
                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount > 20f && isAtTop && !dragTriggered) {
                                dragTriggered = true
                                handleClose()
                                change.consume()
                            }
                        },
                        onDragEnd = {},
                        onDragCancel = {}
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barre de recherche pleine largeur avec placeholder
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    if (!isAnimating) {
                        searchQuery = it
                    }
                },
                onSearch = {},
                active = false,
                onActiveChange = {},
                enabled = !isAnimating,
                placeholder = { Text("Rechercher...") },
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
                    .nestedScroll(nestedScrollConnection)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (filteredApps.isEmpty() && filteredContacts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune application trouvée.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Entrée Paramètres (uniquement quand on ne recherche pas)
                        if (!isSearching) {
                            item(key = "settings_entry") {
                                SettingsDrawerEntry(onClick = onOpenSettings)
                            }
                        }
                        // Section Contacts (uniquement pendant la recherche)
                        if (filteredContacts.isNotEmpty()) {
                            item(key = "contacts_header") {
                                SectionHeader("Contacts")
                            }
                            items(filteredContacts, key = { "contact_${it.name}_${it.phoneNumber}" }) { contact ->
                                ContactDrawerItem(
                                    contact = contact,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${contact.phoneNumber}")
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                )
                            }
                        }

                        // Header "Applications" pendant la recherche
                        if (isSearching && filteredApps.isNotEmpty()) {
                            item(key = "apps_header") {
                                SectionHeader("Applications")
                            }
                        }

                        // Liste des apps groupées par lettre
                        groupedApps.forEach { (letter, appsInGroup) ->
                            item(key = "header_$letter") {
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
                                    onClick = { launchApp(context, appInfo) }
                                )
                            }
                        }
                    }

                    // Index alphabétique - overlay sur la droite, apparition animée
                    if (!isSearching && groupedApps.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(48.dp)
                                .fillMaxHeight()
                        ) {
                            AlphabetIndex(
                                groupedApps = groupedApps,
                                letterToItemIndex = letterToItemIndex,
                                listState = listState,
                                alpha = indexAlpha.value,
                                onShow = {
                                    coroutineScope.launch {
                                        indexAlpha.snapTo(1f)
                                    }
                                },
                                onLetterSelected = { letter ->
                                    val itemIndex = letterToItemIndex[letter] ?: return@AlphabetIndex
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(itemIndex)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )
}

/**
 * Index alphabétique vertical sur le côté droit.
 * Pleine hauteur, apparition animée au scroll ou au toucher du bord droit.
 * Taper ou glisser sur une lettre fait défiler la liste vers cette section.
 */
@Composable
fun AlphabetIndex(
    groupedApps: Map<String, List<AppInfo>>,
    letterToItemIndex: Map<String, Int>,
    listState: LazyListState,
    alpha: Float,
    onShow: () -> Unit,
    onLetterSelected: (String) -> Unit
) {
    val letters = remember(groupedApps) {
        groupedApps.keys.toSortedSet(String.CASE_INSENSITIVE_ORDER).toList()
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                RoundedCornerShape(8.dp)
            )
            .padding(vertical = 4.dp)
            .pointerInput(letters) {
                val letterHeight = size.height / letters.size
                detectTapGestures { offset ->
                    onShow()
                    val letterIndex = (offset.y / letterHeight).toInt()
                        .coerceIn(0, letters.lastIndex)
                    onLetterSelected(letters[letterIndex])
                }
            }
            .pointerInput(letters) {
                val letterHeight = size.height / letters.size
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        onShow()
                        val letterIndex = (offset.y / letterHeight).toInt()
                            .coerceIn(0, letters.lastIndex)
                        onLetterSelected(letters[letterIndex])
                    },
                    onVerticalDrag = { change, _ ->
                        val letterIndex = (change.position.y / letterHeight).toInt()
                            .coerceIn(0, letters.lastIndex)
                        onLetterSelected(letters[letterIndex])
                        change.consume()
                    }
                )
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        letters.forEach { letter ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Élément de contact dans le tiroir.
 */
@Composable
fun ContactDrawerItem(contact: ContactInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = contact.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Élément d'application dans le tiroir.
 */
@Composable
fun AppDrawerItem(appInfo: AppInfo, onClick: () -> Unit) {
    val stableAppInfo = remember(appInfo) { appInfo }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stableAppInfo.appName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Entrée "Paramètres du launcher" en tête du tiroir.
 */
@Composable
fun SettingsDrawerEntry(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Paramètres",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Paramètres du launcher",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
