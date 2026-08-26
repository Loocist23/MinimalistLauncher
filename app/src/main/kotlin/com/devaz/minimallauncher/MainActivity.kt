package com.devaz.minimallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    val minThreshold = 10f       // Seuil minimal pour éviter les déclenchements accidentels
    val fullOpenThreshold = 100f // Seuil pour ouverture complète
    
    // Offset Y animé du tiroir
    // screenHeight = complètement caché (en bas de l'écran)
    // 0 = complètement visible
    val drawerOffset = remember { Animatable(screenHeight) }
    
    // Tracker pour savoir si on est en train d'interagir
    val isInteracting = remember { mutableStateOf(false) }
    
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

    if (!hasPermission) {
        PermissionScreen(
            onPermissionChecked = {
                hasPermission = activity.checkQueryAllPackagesPermission()
            }
        )
    } else if (isLoading) {
        // État de chargement initial - afficher un indicateur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // Shimmer/Placeholder pour une expérience premium
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Indicateurs de chargement pour les apps
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(vertical = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(24.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Indicateurs de progression
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Chargement des applications...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        // Contenu principal - apps chargées
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {},
                        onVerticalDrag = { change, dragAmount ->
                            if (isInteracting.value) {
                                change.consume()
                                return@detectVerticalDragGestures
                            }
                            
                            // dragAmount > 0 = vers le bas, dragAmount < 0 = vers le haut
                            val newOffset = (drawerOffset.value + dragAmount).coerceIn(0f, screenHeight)
                            coroutineScope.launch {
                                drawerOffset.snapTo(newOffset)
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            val currentValue = drawerOffset.value
                            val halfWay = screenHeight / 2f
                            
                            if (currentValue > halfWay) {
                                closeDrawer()
                            } else if (currentValue < halfWay) {
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
                        },
                        onDragCancel = {
                            val currentValue = drawerOffset.value
                            val halfWay = screenHeight / 2f
                            
                            if (currentValue > halfWay) {
                                closeDrawer()
                            } else if (currentValue < halfWay) {
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
                    onScrollStarted = { isInteracting.value = true },
                    onScrollStopped = { isInteracting.value = false },
                    isAnimating = isInteracting.value
                )
            }
        }
    }
}
