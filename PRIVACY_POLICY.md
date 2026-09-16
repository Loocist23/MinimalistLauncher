# Politique de confidentialité — MinimalLauncher

**Nom de l'application :** MinimalLauncher  
**Package :** `com.devaz.minimallauncher`  
**Version :** 1.2.5  
**Type :** Launcher (écran d'accueil Android)

---

## Description de l'application

MinimalLauncher est un launcher minimaliste pour Android. Il affiche l'heure et la date en temps réel, des applications favorites personnalisables (4 emplacements + horloge), et un tiroir d'applications avec recherche alphabétique et tri par fréquence d'utilisation.

---

## Données collectées

**Aucune donnée personnelle n'est collectée, stockée ou transmise.**

MinimalLauncher fonctionne entièrement en local sur l'appareil. Aucune donnée n'est envoyée vers un serveur externe, un service tiers ou le cloud.

---

## Permissions utilisées

| Permission | Utilisation |
|---|---|
| `QUERY_ALL_PACKAGES` | Lister les applications installées (nom, nom de package, icône) pour l'affichage dans le tiroir d'applications |
| `PACKAGE_USAGE_STATS` | Trier les applications par fréquence d'utilisation (temps en premier plan sur les 30 derniers jours) |

Ces permissions sont nécessaires au fonctionnement de l'application. Les données auxquelles elles permettent d'accéder restent en mémoire vive (RAM) et sont détruites à la fermeture de l'application. **Rien n'est persisté sur l'appareil ni envoyé ailleurs.**

---

## Ce que l'application ne fait pas

- **Aucune permission INTERNET** — aucun envoi de données vers un serveur.
- **Aucune base de données** — aucune donnée sauvegardée sur l'appareil (pas de SQLite, Room, SharedPreferences ou DataStore).
- **Aucun analytics, crashlytics, publicité, tracking ou télémétrie.**
- **Aucune donnée persistée** — tout est en mémoire volatile, perdue à la fermeture.
- **Aucun partage de données avec des tiers.**

---

## Données accessibles par l'application

1. **Liste des applications installées** — nom, nom de package et icône. Utilisée uniquement pour l'affichage à l'écran. Non sauvegardée.
2. **Statistiques d'utilisation des applications** — temps en premier plan par application, sur les 30 derniers jours. Utilisées uniquement pour trier les applications par fréquence. Non sauvegardées.

---

## Confidentialité des enfants

MinimalLauncher ne cible pas spécifiquement les enfants et ne collecte aucune donnée personnelle, quelle que soit l'âge de l'utilisateur.

---

## Modifications de cette politique

Cette politique peut être mise à jour si l'application évolue. Toute modification sera indiquée dans cette page avec la date correspondante.

---

## Contact

Pour toute question relative à cette politique de confidentialité, vous pouvez contacter le développeur à l'adresse associée au compte Google Play de l'application.

---

*Dernière mise à jour : 15 septembre 2026*
