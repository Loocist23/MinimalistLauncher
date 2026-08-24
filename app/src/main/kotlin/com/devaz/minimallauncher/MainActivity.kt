package com.devaz.minimallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.devaz.minimallauncher.ui.AppDrawer
import com.devaz.minimallauncher.ui.HomeScreen
import com.devaz.minimallauncher.ui.PermissionScreen
import com.devaz.minimallauncher.ui.theme.MinimalLauncherTheme
import kotlinx.coroutines.delay

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
    
    // État du tiroir
    val isDrawerOpen = remember { mutableStateOf(false) }
    
    // Tracker pour savoir si on est en train de scroller dans le AppDrawer
    val isScrollingInDrawer = remember { mutableStateOf(false) }
    
    // Protection contre la fermeture immédiate après ouverture
    val isAnimating = remember { mutableStateOf(false) }
    
    // Gérer l'animation
    LaunchedEffect(isDrawerOpen.value) {
        if (isDrawerOpen.value) {
            isAnimating.value = true
            kotlinx.coroutines.delay(250) // Durée de l'animation
            isAnimating.value = false
        }
    }
    
    // Seuil pour détecter un swipe
    val swipeThreshold = 100f
    
    if (hasPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            // HomeScreen - toujours visible
            // Ajout de la détection de swipe vers le haut pour ouvrir le tiroir
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { change, dragAmount ->
                            if (!isAnimating.value && !isDrawerOpen.value && dragAmount < -swipeThreshold) {
                                // Swipe vers le haut détecté (dragAmount négatif = vers le haut)
                                isDrawerOpen.value = true
                            }
                        }
                    }
            ) {
                HomeScreen(
                    onDrawerOpen = {
                        if (!isAnimating.value) {
                            isDrawerOpen.value = true
                        }
                    }
                )
            }
            
            // AppDrawer - avec animation de slide
            // Prend tout l'écran, donc bloque naturellement les clics sur HomeScreen
            // Ajout de la détection de swipe vers le bas pour fermer
            AnimatedVisibility(
                visible = isDrawerOpen.value,
                enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut(),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { change, dragAmount ->
                            if (!isAnimating.value && isDrawerOpen.value && dragAmount > swipeThreshold) {
                                // Swipe vers le bas détecté (dragAmount positif = vers le bas)
                                isDrawerOpen.value = false
                            }
                        }
                    }
            ) {
                AppDrawer(
                    onClose = {
                        if (!isAnimating.value) {
                            isDrawerOpen.value = false
                        }
                    },
                    onScrollStarted = {
                        isScrollingInDrawer.value = true
                    },
                    onScrollStopped = {
                        isScrollingInDrawer.value = false
                    },
                    // Désactiver les interactions pendant l'animation
                    isAnimating = isAnimating.value
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
