critical bug: CI artifact is broken (?).
im getting: Fail to load plugin descriptor from file intellij-git-notes-1.0.5.zip.
workaround: build locally.

task: add e2e test.

bug: topic autocomplete is getting polluted over time by old/stale topics.
improve this situation/find solution.

visual bug: email VCS column has no "hovered" effect when the mouse overlaps the row.
why?

task: "edit note" window should have clickable links like `CONFLICT (content): community/android/project-system-gradle/src/com/android/tools/idea/projectsystem/gradle/sync/AndroidModuleDataService.kt` for easy navigation.
alt+left click maybe?

task to consider: how note changes over time? all note changes are materialized as commits under the hood.
so in theory, we should be able to show versions (content diff?).

task: add demo project. it's getting more important over time because monorepo is too heavy and runIde requires reindexing. 
