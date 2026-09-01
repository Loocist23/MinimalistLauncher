# 🧪 Liste de Tests - Minimal Launcher

**Version** : `4aee596`  
**Date** : 01/09/2026  
**Fonctionnalités testées** : Heure en temps réel, Gestion du tiroir d'apps, Personnalisation de l'horloge

---

## 📌 **INSTRUCTIONS**

### Avant de commencer
- [ ] Installer l'APK sur ton appareil
- [ ] Noter le **modèle du téléphone** et la **version d'Android**
- [ ] Vérifier que les permissions sont accordées

### Comment signaler un bug ?
```
Test # : [Numéro du test]
Appareil : [Modèle + Version Android]
Description : [Ce qui ne fonctionne pas]
Étapes pour reproduire : [Comment faire pour déclencher le bug]
```

---

## 🕐 **TESTS POUR L'HEURE**

| # | Description | Résultat attendu | ✅ |
|---|-------------|------------------|---|
| 1 | **Mise à jour automatique** | L'heure se rafraîchit toutes les secondes (regarder la seconde changer) | ⬜ |
| 2 | **Cliquer sur l'heure** | Ouvre l'application d'horloge du téléphone | ⬜ |
| 3 | **Long-press sur l'heure** | Un menu apparaît avec "Informations" et "Modifier l'application" | ⬜ |
| 4 | **Modifier → Liste des apps** | Seules les apps d'horloge s'affichent (ex: Horloge, Google Clock) | ⬜ |
| 5 | **Modifier → Sélectionner une app** | L'icône et le nom de l'heure changent, cliquer ouvre la nouvelle app | ⬜ |
| 6 | **Informations sur l'heure** | Affiche un dialog avec le nom et le package de l'app | ⬜ |

---

## 📱 **TESTS POUR LE TIROIR D'APPS**

| # | Description | Résultat attendu | ✅ |
|---|-------------|------------------|---|
| 7 | **Ouvrir le tiroir** | Swipe **vers le haut** depuis n'importe où → tiroir s'ouvre | ⬜ |
| 8 | **Fermer le tiroir (tout en haut)** | Swipe **vers le bas** depuis n'importe où **QUAND ON EST TOUT EN HAUT DE LA LISTE** → tiroir se ferme | ⬜ |
| 9 | **Fermer le tiroir (en scrollant)** | Si on a scrollé vers le bas dans la liste, swipe vers le bas **NE FERME PAS** le tiroir, il fait scroller la liste | ⬜ |
| 10 | **Bouton Fermer (X)** | Cliquer sur la croix en haut à droite → tiroir se ferme | ⬜ |
| 11 | **Bouton "Retour en haut"** | Cliquer sur l'icône en bas à droite → scroll instantané vers le haut | ⬜ |
| 12 | **Recherche dans le tiroir** | Taper "you" → filtre et affiche YouTube (et autres apps correspondantes) | ⬜ |
| 13 | **Index alphabétique** | Cliquer sur "Y" → scroll vers YouTube | ⬜ |
| 14 | **Index → Pas d apps** | Cliquer sur une lettre sans app (ex: "Z") → rien ne se passe | ⬜ |

---

## ⭐ **TESTS POUR LES APPS FAVORITES**

| # | Description | Résultat attendu | ✅ |
|---|-------------|------------------|---|
| 15 | **Cliquer sur une app favorite** | Ouvre l'application correspondante | ⬜ |
| 16 | **Long-press sur une app** | Menu apparaît avec "Informations" et "Modifier" | ⬜ |
| 17 | **Modifier une app (Position 0)** | Affiche uniquement des apps de **téléphone/appels** | ⬜ |
| 18 | **Modifier une app (Position 1)** | Affiche uniquement des apps de **réseaux sociaux** | ⬜ |
| 19 | **Modifier une app (Position 2)** | Affiche uniquement des apps de **stores** | ⬜ |
| 20 | **Modifier une app (Position 3)** | Affiche uniquement des apps **vidéo** | ⬜ |
| 21 | **Modifier une app (Position 4)** | Affiche uniquement des apps **d'horloge** | ⬜ |
| 22 | **Sélectionner une nouvelle app** | L'icône et le nom changent, cliquer ouvre la nouvelle app | ⬜ |
| 23 | **Annuler la modification** | Cliquer sur "Annuler" → retourne sans changement | ⬜ |
| 24 | **Infos sur une app** | Affiche nom + package de l'app | ⬜ |

---

## ⚡ **TESTS DE PERFORMANCE / EDGE CASES**

| # | Description | Résultat attendu | ✅ |
|---|-------------|------------------|---|
| 25 | **Ouvrir/fermer rapidement** | Pas de bug, pas de double animation | ⬜ |
| 26 | **Swipe très court** | Ne déclenche pas l'ouverture/fermeture | ⬜ |
| 27 | **Swipe pendant l'animation** | Ne déclenche pas d'action pendant l'animation | ⬜ |
| 28 | **Swipe pendant le scroll** | Le scroll fonctionne normalement | ⬜ |
| 29 | **Rotation écran** | L'heure continue à se mettre à jour, le tiroir fonctionne | ⬜ |
| 30 | **Changer le thème** | Les couleurs s'adaptent (clair/sombre) | ⬜ |
| 31 | **Appui très rapide** | Pas de double déclenchement | ⬜ |

---

## 🎯 **APPS D'HORLOGE À TESTER**

Si tu as une de ces apps installées, teste le changement de l'app d'horloge :

| App | Package | Disponible ? |
|-----|---------|--------------|
| Horloge (Google) | `com.google.android.deskclock` | ⬜ |
| Horloge (AOSP) | `com.android.deskclock` | ⬜ |
| Horloge Samsung | `com.sec.android.app.clockpackage` | ⬜ |
| Horloge Xiaomi | `com.miui.clock` | ⬜ |
| Horloge OnePlus | `com.oneplus.clock` | ⬜ |
| Horloge OPPO | `com.nevision.nevisionclock` | ⬜ |

---

## 📊 **RÉSULTATS**

**Nombre total de tests** : 31  
**Tests passés** : ___ / 31  
**Tests échoués** : ___ / 31  

---

### Liste des bugs trouvés

```

```

---

## 💡 **ASTUCES**

- **Pour forcer un rafraîchissement** : Ferme et rouvre le launcher
- **Pour voir l'heure changer** : Attends 1 seconde entre chaque vérification
- **Pour tester le swipe** : Fais un mouvement **rapide et net**
- **Pour scroller** : Fais un mouvement **lent et maintenu**

---

**Merci pour ton aide !** 🎉  
*Si tu trouves des bugs, contacte @loocist avec les infos ci-dessus.*
