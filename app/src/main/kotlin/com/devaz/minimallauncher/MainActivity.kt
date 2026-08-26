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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
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
    LaunchedEffect(Unit) {
        viewModel.loadApps()
    }
    
    // Calculer la hauteur de l'écran en pixels
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    // Seuils
    val minThreshold = 20f       // Seuil minimal pour éviter les déclenchements accidentels
    val fullOpenThreshold = 200f // Seuil pour ouverture complète
    
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

    if (hasPermission) {
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
    } else {
        PermissionScreen(
            onPermissionChecked = {
                hasPermission = activity.checkQueryAllPackagesPermission()
            }
        )
    }
}
