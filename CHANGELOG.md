# Changelog — MinimalLauncher

## [1.3.0] — 2026-09-16

### Corrections Google Play (edge-to-edge + grand écran)

- `compileSdk` 34 → 35 sur tous les modules (app, core, data)
- Activation de `enableEdgeToEdge()` dans `MainActivity.onCreate()`
- Suppression de `window.statusBarColor` (API dépréciée Android 15) dans `Theme.kt`
- Suppression de `android:screenOrientation="portrait"` et `resizeableActivity` dans le manifest
- Élargissement de `configChanges` avec `orientation|screenLayout|smallestScreenSize`

### Tiroir d'applications

- Placeholder "Rechercher..." dans la barre de recherche
- Recherche de contacts intégrée : les contacts apparaissent dans les résultats de recherche avec nom et numéro, tap = ouverture du dialer
- Suppression des boutons de fermeture (croix) dans le tiroir
- Index alphabétique pleine hauteur avec apparition animée au scroll ou au toucher du bord droit
- Scroll animé par lettre (tap ou drag sur l'index)
- Toggle de recherche de contacts (activable/désactivable depuis les paramètres)
- Réinitialisation de la recherche et du scroll à la fermeture du tiroir
- Fermeture du tiroir par scroll vers le bas depuis la liste (pull-to-close via nestedScroll), fonctionne même en glissant sur des apps, sauf sur l'index alphabétique

### Permissions

- Demande runtime de la permission `READ_CONTACTS` au lancement de l'app (après validation de `QUERY_ALL_PACKAGES`)

### Paramètres du launcher

- Nouvel écran de paramètres accessible depuis une entrée "Paramètres du launcher" en tête du tiroir
- **Apparence** : thème (système/clair/sombre), couleur d'accent (7 couleurs), taille de l'horloge (slider 40-120 sp)
- **Applications favorites** : nombre d'apps (4/6/8), réinitialisation des apps favorites
- **Contacts** : activation/désactivation de la recherche de contacts
- Persistance via SharedPreferences (`SettingsRepository`)
- Application en temps réel du thème et de la couleur d'accent via `MinimalLauncherTheme`

### Nouveaux fichiers

- `core/.../model/ContactInfo.kt` — modèle de contact
- `data/.../repository/SettingsRepository.kt` — persistance des paramètres
- `app/.../viewmodel/SettingsViewModel.kt` — ViewModel des paramètres
- `app/.../ui/SettingsScreen.kt` — écran de paramètres

### Fichiers modifiés

- `app/build.gradle.kts` — compileSdk 35
- `core/build.gradle.kts` — compileSdk 35
- `data/build.gradle.kts` — compileSdk 35
- `app/src/main/AndroidManifest.xml` — suppression restrictions orientation, configChanges étendu
- `app/.../MainActivity.kt` — enableEdgeToEdge, navigation vers les paramètres
- `app/.../ui/AppDrawer.kt` — réécriture (placeholder, contacts, index animé, entrée paramètres)
- `app/.../ui/HomeScreen.kt` — taille d'horloge et nombre d'apps depuis les settings
- `app/.../ui/theme/Theme.kt` — thème dynamique (mode + accent)
- `app/.../viewmodel/AppViewModel.kt` — chargement des contacts
- `data/.../repository/AppRepository.kt` — `getContacts()`, fix nullable `applicationInfo` pour SDK 35

---

## [1.2.5] — 2026-09-01

- Correction de l'heure et du tiroir
- Personnalisation de l'horloge
- Personnalisation des apps favorites par catégorie (long-press)

---

## [1.2.0] — 2026-08-27

- Menu de personnalisation sur long-press des apps favorites
- Tiroir d'applications avec index alphabétique

---

## [1.0.0] — 2026-08-24

- Version initiale du launcher
- Écran d'accueil avec horloge et apps favorites
- Tiroir d'applications avec recherche
- Permission QUERY_ALL_PACKAGES
