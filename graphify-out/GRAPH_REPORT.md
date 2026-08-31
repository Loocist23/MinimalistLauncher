# Graph Report - Launcher  (2026-08-27)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 91 nodes · 154 edges · 17 communities (14 shown, 3 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 2 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `462bc993`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity.kt
- AppInfo
- AppDrawer.kt
- AppIconCache
- LauncherScreen.kt
- AppRepository
- MinimalLauncherApplication
- gradlew
- ExampleInstrumentedTest
- ExampleUnitTest

## God Nodes (most connected - your core abstractions)
1. `AppInfo` - 19 edges
2. `AppViewModel` - 14 edges
3. `AppRepository` - 8 edges
4. `AppContent()` - 7 edges
5. `AppDrawer()` - 7 edges
6. `NestedScrollConnection` - 6 edges
7. `AppIconCache` - 6 edges
8. `HomeScreen()` - 5 edges
9. `launchApp()` - 5 edges
10. `MainActivity` - 4 edges

## Surprising Connections (you probably didn't know these)
- `AppViewModel` --calls--> `AppRepository`  [EXTRACTED]
  app/src/main/kotlin/com/devaz/minimallauncher/viewmodel/AppViewModel.kt → data/src/main/kotlin/com/devaz/minimallauncher/repository/AppRepository.kt
- `AlphabetIndex()` --references--> `AppInfo`  [EXTRACTED]
  app/src/main/kotlin/com/devaz/minimallauncher/ui/AppDrawer.kt → core/src/main/kotlin/com/devaz/minimallauncher/model/AppInfo.kt
- `AppDrawerItem()` --references--> `AppInfo`  [EXTRACTED]
  app/src/main/kotlin/com/devaz/minimallauncher/ui/AppDrawer.kt → core/src/main/kotlin/com/devaz/minimallauncher/model/AppInfo.kt
- `AppItem()` --references--> `AppInfo`  [EXTRACTED]
  app/src/main/kotlin/com/devaz/minimallauncher/ui/LauncherScreen.kt → core/src/main/kotlin/com/devaz/minimallauncher/model/AppInfo.kt
- `launchApp()` --references--> `AppInfo`  [EXTRACTED]
  app/src/main/kotlin/com/devaz/minimallauncher/ui/Utils.kt → core/src/main/kotlin/com/devaz/minimallauncher/model/AppInfo.kt

## Import Cycles
- None detected.

## Communities (17 total, 3 thin omitted)

### Community 0 - "MainActivity.kt"
Cohesion: 0.20
Nodes (13): AppContent(), LoadingDotsAnimation(), LoadingScreen(), MainActivity, FavoriteApp, FavoriteAppItem(), FavoriteAppsGrid(), HomeScreen() (+5 more)

### Community 1 - "AppInfo"
Cohesion: 0.27
Nodes (5): AndroidViewModel, AppViewModel, Comparable, AppInfo, LiveData

### Community 2 - "AppDrawer.kt"
Cohesion: 0.33
Nodes (7): androidx, AlphabetIndex(), AppDrawer(), NestedScrollConnection, AppDrawerItem(), LazyListState, NestedScrollSource

### Community 3 - "AppIconCache"
Cohesion: 0.33
Nodes (4): AppIconCache, Drawable, Bitmap, ImageBitmap

### Community 4 - "LauncherScreen.kt"
Cohesion: 0.39
Nodes (7): AppIcon(), AppItem(), Drawable, LauncherScreen(), launchApp(), launchAppByPackage(), Context

### Community 5 - "AppRepository"
Cohesion: 0.36
Nodes (3): android, AppRepository, PackageManager

### Community 7 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AppViewModel` connect `AppInfo` to `MainActivity.kt`, `AppDrawer.kt`, `LauncherScreen.kt`, `AppRepository`?**
  _High betweenness centrality (0.248) - this node is a cross-community bridge._
- **Why does `AppInfo` connect `AppInfo` to `AppDrawer.kt`, `LauncherScreen.kt`, `AppRepository`?**
  _High betweenness centrality (0.166) - this node is a cross-community bridge._
- **Why does `AppRepository` connect `AppRepository` to `AppInfo`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._