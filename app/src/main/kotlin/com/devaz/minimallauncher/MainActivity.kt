package com.devaz.minimallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.devaz.minimallauncher.ui.AppDrawer
import com.devaz.minimallauncher.ui.HomeScreen
import com.devaz.minimallauncher.ui.PermissionScreen
import com.devaz.minimallauncher.ui.theme.MinimalLauncherTheme
import com.devaz.minimallauncher.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MinimalLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent()
                }
            }
        }
    }
    
    fun checkQueryAllPackagesPermission(): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                packageManager.getInstalledPackages(0)
                true
            } else {
                true
            }
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Écran de chargement premium avec animations fluides
 */
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Logo/icône du launcher avec animation pulse
            val pulseScale = remember { Animatable(1f) }
            LaunchedEffect(Unit) {
                while (true) {
                    pulseScale.animateTo(
                        1.15f,
                        animationSpec = tween(800, easing = LinearEasing)
                    )
                    pulseScale.animateTo(
                        1f,
                        animationSpec = tween(800, easing = LinearEasing)
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer { 
                        scaleX = pulseScale.value
                        scaleY = pulseScale.value
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.extraLarge
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Animation de points de chargement (loading dots)
            LoadingDotsAnimation()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Chargement des applications...",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Animation de 3 points qui apparaissent en séquence
 */
@Composable
fun LoadingDotsAnimation() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.height(30.dp)
    ) {
        repeat(3) { index ->
            val delay = index * 150L
            val alpha = remember { Animatable(0f) }
            val scale = remember { Animatable(0.5f) }
            
            LaunchedEffect(Unit) {
                while (true) {
                    // Animer alpha et scale en parallèle
                    alpha.animateTo(1f, animationSpec = tween(400, delayMillis = delay.toInt()))
                    scale.animateTo(1f, animationSpec = tween(400, delayMillis = delay.toInt()))
                    
                    alpha.animateTo(0f, animationSpec = tween(400, delayMillis = (1000 - delay).toInt()))
                    scale.animateTo(0.5f, animationSpec = tween(400, delayMillis = (1000 - delay).toInt()))
                }
            }
            
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .graphicsLayer { 
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    }
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.primary)
            )
            
            if (index < 2) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

// Énumération pour la direction du swipe
enum class DragDirection { UP, DOWN, NONE }

@Composable
fun AppContent() {
    val activity = LocalContext.current as MainActivity
    var hasPermission by remember { mutableStateOf(activity.checkQueryAllPackagesPermission()) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    // Charger les apps dès l'ouverture du launcher (pas seulement quand on ouvre le tiroir)
    val viewModel: AppViewModel = viewModel()
    val isLoading by viewModel.isLoading.observeAsState(true)
    
    LaunchedEffect(Unit) {
        viewModel.loadApps()
    }
    
    // Calculer la hauteur de l'écran en pixels
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    // Seuils
    val minThreshold = 20f       // Seuil minimal pour éviter les déclenchements accidentels
    val fullOpenThreshold = 100f // Seuil pour ouverture complète
    
    // Offset Y animé du tiroir
    // screenHeight = complètement caché (en haut de l'écran)
    // 0 = complètement visible
    val drawerOffset = remember { Animatable(screenHeight) }
    
    // Tracker pour savoir si on est en train d'interagir
    val isInteracting = remember { mutableStateOf(false) }
    
    // Tracker pour la direction du swipe
    val lastDragDirection = remember { mutableStateOf(DragDirection.NONE) }
    
    // Fermer complètement le tiroir (appelée par AppDrawer)
    fun closeDrawer() {
        coroutineScope.launch {
            isInteracting.value = true
            drawerOffset.animateTo(
                targetValue = screenHeight,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            isInteracting.value = false
        }
    }
    
    // Ouvrir le tiroir en swipant vers le haut depuis n'importe où sur l'écran
    fun openDrawer() {
        coroutineScope.launch {
            isInteracting.value = true
            drawerOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            isInteracting.value = false
        }
    }

    if (!hasPermission) {
        PermissionScreen(
            onPermissionChecked = {
                hasPermission = activity.checkQueryAllPackagesPermission()
            }
        )
    } else if (isLoading) {
        // État de chargement initial - Animation premium
        LoadingScreen()
    } else {
        // Contenu principal - apps chargées
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            // Permettre le démarrage du swipe depuis n'importe où sur l'écran
                            // (comme sur Xiaomi/Samsung)
                        },
                        onVerticalDrag = { change, dragAmount ->
                            if (isInteracting.value) {
                                change.consume()
                                return@detectVerticalDragGestures
                            }
                            
                            // dragAmount > 0 = vers le bas (ferme), dragAmount < 0 = vers le haut (ouvre)
                            // Offset initial = screenHeight (caché)
                            // Swipe vers le haut depuis le haut = offset diminue = tiroir monte = OUVRE
                            val newOffset = (drawerOffset.value + dragAmount).coerceIn(0f, screenHeight)
                            coroutineScope.launch {
                                drawerOffset.snapTo(newOffset)
                            }
                            
                            // Mémoriser la direction du swipe
                            if (dragAmount < 0) {
                                lastDragDirection.value = DragDirection.UP
                            } else if (dragAmount > 0) {
                                lastDragDirection.value = DragDirection.DOWN
                            }
                            
                            change.consume()
                        },
                        onDragEnd = {
                            // On vérifie la direction du swipe, pas la position finale
                            // dragAmount < 0 = swipe vers le haut = ouvrir
                            // dragAmount > 0 = swipe vers le bas = fermer
                            // On utilise un état pour mémoriser la direction du swipe
                            if (lastDragDirection.value == DragDirection.UP) {
                                openDrawer()
                            } else if (lastDragDirection.value == DragDirection.DOWN) {
                                closeDrawer()
                            }
                            // Réinitialiser la direction
                            lastDragDirection.value = DragDirection.NONE
                        },
                        onDragCancel = {
                            // Réinitialiser la direction et fermer le tiroir
                            lastDragDirection.value = DragDirection.NONE
                            closeDrawer()
                        }
                    )
                }
        ) {
            // HomeScreen - toujours visible
            HomeScreen()
            
            // AppDrawer - toujours rendu (pré-rendu) mais avec offset animé
            // GPU-accéléré via offset pour 120 FPS
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, drawerOffset.value.toInt()) }
            ) {
                AppDrawer(
                    onClose = { closeDrawer() },
                    onOpen = { openDrawer() },
                    onScrollStarted = { isInteracting.value = true },
                    onScrollStopped = { isInteracting.value = false },
                    isAnimating = isInteracting.value
                )
            }
        }
    }
}
