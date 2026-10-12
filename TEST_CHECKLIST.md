# Liste de Tests - Minimal Launcher

**Version** : `1.4.2`  
**Date** : 12/10/2026  
**Fonctionnalites testees** : Heure en temps reel, Gestion du tiroir d'apps, Rechargement des apps, Menu contextuel long-clic, Personnalisation

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
| 10 | Recherche dans le tiroir | Taper "you" -> filtre et affiche YouTube (et autres apps correspondantes) |   |
| 11 | Reinitialisation de la recherche | Fermer le tiroir puis le rouvrir -> recherche vide et liste en haut |   |
| 12 | Index alphabetique | Cliquer sur "Y" -> scroll vers YouTube |   |
| 13 | Index -> Pas d apps | Cliquer sur une lettre sans app (ex: "Z") -> rien ne se passe |   |
| 14 | Recherche de contacts | Taper le nom d'un contact -> il apparait avec son numero, cliquer ouvre le dialer |   |

---

## TESTS POUR LE RECHARGEMENT DES APPS (1.4.2)

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 15 | Nouvelle app installee | Launcher ouvert, installer une app (ex: via Android Studio) -> ouvrir le tiroir -> la nouvelle app apparait SANS relancer le launcher |   |
| 16 | App desinstallee | Desinstaller une app -> rouvrir le tiroir -> l'app n'apparait plus |   |
| 17 | Pas d'ecran de chargement | Ouvrir le tiroir -> la liste s'affiche directement, pas de spinner ni d'ecran de chargement pendant le refresh |   |

---

## TESTS POUR LE MENU CONTEXTUEL LONG-CLIC (1.4.2)

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 18 | Long-clic sur une app du tiroir | Retour haptique + menu avec "Informations de l'application" et "Desinstaller" |   |
| 19 | Informations de l'application | Ouvre l'ecran systeme App info (permissions, stockage, forcer l'arret, effacer les donnees, notifications) |   |
| 20 | Desinstaller via le menu | Ouvre la confirmation systeme ; confirmer -> l'app est desinstallee |   |
| 21 | Fermer le menu | Taper a cote du menu -> il se ferme sans action |   |

---

## TESTS POUR LES APPS FAVORITES

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 22 | Cliquer sur une app favorite | Ouvre l'application correspondante |   |
| 23 | Long-press sur une app | Menu apparait avec "Informations" et "Modifier" |   |
| 24 | Modifier une app (Position 0) | Affiche uniquement des apps de telephone/appels |   |
| 25 | Modifier une app (Position 1) | Affiche uniquement des apps de reseaux sociaux |   |
| 26 | Modifier une app (Position 2) | Affiche uniquement des apps de stores |   |
| 27 | Modifier une app (Position 3) | Affiche uniquement des apps video |   |
| 28 | Modifier une app (Position 4) | Affiche uniquement des apps d'horloge |   |
| 29 | Selectionner une nouvelle app | L'icone et le nom changent, cliquer ouvre la nouvelle app |   |
| 30 | Annuler la modification | Cliquer sur "Annuler" -> retourne sans changement |   |
| 31 | Infos sur une app | Affiche nom + package de l'app |   |

---

## TESTS POUR LES PARAMETRES DU LAUNCHER (1.3.0 / 1.4.0)

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 32 | Entree parametres | "Parametres du launcher" en tete du tiroir -> ouvre l'ecran de parametres |   |
| 33 | Theme | Système / clair / sombre applique en temps reel |   |
| 34 | Couleur d'accent | 7 couleurs disponibles, appliquees en temps reel |   |
| 35 | Taille de l'horloge | Slider 40-120 sp, applique en temps reel |   |
| 36 | Nombre d apps favorites | 4 apps = 1 ligne, 6 apps = 2 lignes de 3, 8 apps = 2 lignes de 4 |   |
| 37 | Reinitialiser les favorites | Bouton -> les apps favorites reviennent par defaut |   |
| 38 | Toggle recherche de contacts | Desactive -> les contacts n'apparaissent plus dans la recherche du tiroir |   |

---

## TESTS DE PERFORMANCE / EDGE CASES

| # | Description | Resultat attendu | OK |
|---|-------------|------------------|---|
| 39 | Ouvrir/fermer rapidement | Pas de bug, pas de double animation |   |
| 40 | Swipe tres court | Ne declenche pas l'ouverture/fermeture |   |
| 41 | Swipe pendant l'animation | Ne declenche pas d'action pendant l'animation |   |
| 42 | Swipe pendant le scroll | Le scroll fonctionne normalement |   |
| 43 | Rotation ecran | L'heure continue a se mettre a jour, le tiroir fonctionne |   |
| 44 | Changer le theme | Les couleurs s'adaptent (clair/sombre) |   |
| 45 | Appui tres rapide | Pas de double declenchement |   |

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

**Nombre total de tests** : 45  
**Tests passes** : ___ / 45  
**Tests echoues** : ___ / 45  

---

### Liste des bugs trouves



---

## ASTUCES

- Pour forcer un rafraichissement des apps : Ferme et rouvre le tiroir
- Pour voir l'heure changer : Attends 1 seconde entre chaque verification
- Pour tester le swipe : Fais un mouvement rapide et net
- Pour scroller : Fais un mouvement lent et maintenu

---

**Merci pour ton aide !**  
*Si tu trouves des bugs, contacte @loocist avec les infos ci-dessus.*
