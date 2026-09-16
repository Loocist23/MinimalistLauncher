package com.devaz.minimallauncher.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devaz.minimallauncher.viewmodel.SettingsViewModel

// Couleurs d'accent prédéfinies
data class AccentColorOption(val key: String, val label: String, val color: Color)

val accentColors = listOf(
    AccentColorOption("purple", "Violet", Color(0xFF6650a4)),
    AccentColorOption("blue", "Bleu", Color(0xFF1976D2)),
    AccentColorOption("teal", "Turquoise", Color(0xFF00897B)),
    AccentColorOption("green", "Vert", Color(0xFF2E7D32)),
    AccentColorOption("orange", "Orange", Color(0xFFE65100)),
    AccentColorOption("red", "Rouge", Color(0xFFC62828)),
    AccentColorOption("pink", "Rose", Color(0xFFAD1457))
)

fun getAccentColor(key: String): Color =
    accentColors.firstOrNull { it.key == key }?.color ?: Color(0xFF6650a4)

/**
 * Écran des paramètres du launcher.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val themeMode by settingsViewModel.themeMode.observeAsState("system")
    val accentColorKey by settingsViewModel.accentColorKey.observeAsState("purple")
    val clockFontSize by settingsViewModel.clockFontSize.observeAsState(72)
    val favoriteAppsCount by settingsViewModel.favoriteAppsCount.observeAsState(4)
    val contactsSearchEnabled by settingsViewModel.contactsSearchEnabled.observeAsState(true)

    var showResetDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barre supérieure avec bouton retour
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Paramètres du launcher",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Contenu défilant
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section : Apparence
                SettingsSection(title = "Apparence") {
                    // Thème
                    Text(
                        text = "Thème",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "system" to "Système",
                            "light" to "Clair",
                            "dark" to "Sombre"
                        ).forEach { (key, label) ->
                            val isSelected = themeMode == key
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { settingsViewModel.setThemeMode(key) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Couleur d'accent
                    Text(
                        text = "Couleur d'accent",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        accentColors.forEach { option ->
                            val isSelected = accentColorKey == option.key
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(option.color)
                                    .clickable { settingsViewModel.setAccentColorKey(option.key) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Taille de l'horloge
                    Text(
                        text = "Taille de l'horloge : ${clockFontSize} sp",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Slider(
                        value = clockFontSize.toFloat(),
                        onValueChange = { settingsViewModel.setClockFontSize(it.toInt()) },
                        valueRange = 40f..120f,
                        steps = 15
                    )
                }

                // Section : Applications favorites
                SettingsSection(title = "Applications favorites") {
                    Text(
                        text = "Nombre d'apps sur l'écran d'accueil",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(4, 6, 8).forEach { count ->
                            val isSelected = favoriteAppsCount == count
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { settingsViewModel.setFavoriteAppsCount(count) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = count.toString(),
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bouton réinitialiser
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "Réinitialiser les apps favorites",
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                // Section : Contacts
                SettingsSection(title = "Contacts") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Recherche de contacts",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Afficher les contacts dans les résultats de recherche",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = contactsSearchEnabled,
                            onCheckedChange = { settingsViewModel.setContactsSearchEnabled(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Version
                Text(
                    text = "MinimalLauncher v1.2.5",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogue de confirmation pour reset
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Réinitialiser") },
            text = { Text("Voulez-vous vraiment réinitialiser les apps favorites ?") },
            confirmButton = {
                TextButton(onClick = {
                    settingsViewModel.resetFavorites()
                    showResetDialog = false
                }) {
                    Text("Réinitialiser")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
