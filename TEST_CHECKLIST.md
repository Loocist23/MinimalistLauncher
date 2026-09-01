# Liste de Tests - Minimal Launcher

**Version** : `4aee596`  
**Date** : 01/09/2026  
**Fonctionnalites testees** : Heure en temps reel, Gestion du tiroir d'apps, Personnalisation de l'horloge

---

## INSTRUCTIONS

### Avant de commencer
- [ ] Installer l'APK sur ton appareil
- [ ] Noter le **modele du telephone** et la **version d'Android**
- [ ] Verifier que les permissions sont accordees

### Comment signaler un bug ?
```
Test # : [Numero du test]
Appareil : [Modele + Version Android]
Description : [Ce qui ne fonctionne pas]
Etapes pour reproduire : [Comment faire pour declencher le bug]
```

---

## TESTS POUR L'HEURE

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 1 | Mise a jour automatique | L'heure se rafraichit toutes les secondes (regarder la seconde changer) |   |
| 2 | Cliquer sur l'heure | Ouvre l'application d'horloge du telephone |   |
| 3 | Long-press sur l'heure | Un menu apparait avec "Informations" et "Modifier l'application" |   |
| 4 | Modifier -> Liste des apps | Seules les apps d'horloge s'affichent (ex: Horloge, Google Clock) |   |
| 5 | Modifier -> Selectionner une app | L'icone et le nom de l'heure changent, cliquer ouvre la nouvelle app |   |
| 6 | Informations sur l'heure | Affiche un dialog avec le nom et le package de l'app |   |

---

## TESTS POUR LE TIROIR D'APPS

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 7 | Ouvrir le tiroir | Swipe vers le haut depuis n'importe ou -> tiroir s'ouvre |   |
| 8 | Fermer le tiroir (tout en haut) | Swipe vers le bas depuis n'importe ou QUAND ON EST TOUT EN HAUT DE LA LISTE -> tiroir se ferme |   |
| 9 | Fermer le tiroir (en scrollant) | Si on a scrolle vers le bas dans la liste, swipe vers le bas NE FERME PAS le tiroir, il fait scroller la liste |   |
| 10 | Bouton Fermer (X) | Cliquer sur la croix en haut a droite -> tiroir se ferme |   |
| 11 | Bouton "Retour en haut" | Cliquer sur l'icone en bas a droite -> scroll instantane vers le haut |   |
| 12 | Recherche dans le tiroir | Taper "you" -> filtre et affiche YouTube (et autres apps correspondantes) |   |
| 13 | Index alphabetique | Cliquer sur "Y" -> scroll vers YouTube |   |
| 14 | Index -> Pas d apps | Cliquer sur une lettre sans app (ex: "Z") -> rien ne se passe |   |

---

## TESTS POUR LES APPS FAVORITES

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 15 | Cliquer sur une app favorite | Ouvre l'application correspondante |   |
| 16 | Long-press sur une app | Menu apparait avec "Informations" et "Modifier" |   |
| 17 | Modifier une app (Position 0) | Affiche uniquement des apps de telephone/appels |   |
| 18 | Modifier une app (Position 1) | Affiche uniquement des apps de reseaux sociaux |   |
| 19 | Modifier une app (Position 2) | Affiche uniquement des apps de stores |   |
| 20 | Modifier une app (Position 3) | Affiche uniquement des apps video |   |
| 21 | Modifier une app (Position 4) | Affiche uniquement des apps d'horloge |   |
| 22 | Selectionner une nouvelle app | L'icone et le nom changent, cliquer ouvre la nouvelle app |   |
| 23 | Annuler la modification | Cliquer sur "Annuler" -> retourne sans changement |   |
| 24 | Infos sur une app | Affiche nom + package de l'app |   |

---

## TESTS DE PERFORMANCE / EDGE CASES

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 25 | Ouvrir/fermer rapidement | Pas de bug, pas de double animation |   |
| 26 | Swipe tres court | Ne declenche pas l'ouverture/fermeture |   |
| 27 | Swipe pendant l'animation | Ne declenche pas d'action pendant l'animation |   |
| 28 | Swipe pendant le scroll | Le scroll fonctionne normalement |   |
| 29 | Rotation ecran | L'heure continue a se mettre a jour, le tiroir fonctionne |   |
| 30 | Changer le theme | Les couleurs s'adaptent (clair/sombre) |   |
| 31 | Appui tres rapide | Pas de double declenchement |   |

---

## APPS D'HORLOGE A TESTER

Si tu as une de ces apps installees, teste le changement de l'app d'horloge :

| App | Package | Disponible ? |
|-----|---------|--------------|
| Horloge (Google) | com.google.android.deskclock |   |
| Horloge (AOSP) | com.android.deskclock |   |
| Horloge Samsung | com.sec.android.app.clockpackage |   |
| Horloge Xiaomi | com.miui.clock |   |
| Horloge OnePlus | com.oneplus.clock |   |
| Horloge OPPO | com.nevision.nevisionclock |   |

---

## RESULTATS

**Nombre total de tests** : 31  
**Tests passes** : ___ / 31  
**Tests echoues** : ___ / 31  

---

### Liste des bugs trouves




---

## ASTUCES

- Pour forcer un rafraichissement : Ferme et rouvre le launcher
- Pour voir l'heure changer : Attends 1 seconde entre chaque verification
- Pour tester le swipe : Fais un mouvement rapide et net
- Pour scroller : Fais un mouvement lent et maintenu

---

**Merci pour ton aide !**  
*Si tu trouves des bugs, contacte @loocist avec les infos ci-dessus.*
