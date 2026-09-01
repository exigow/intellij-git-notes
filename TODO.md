critical bug: CI artifact is broken (?).
im getting: Fail to load plugin descriptor from file intellij-git-notes-1.0.5.zip.
workaround: build locally.

bug: can't create a new empty note.

task: add e2e test.

bug: topic autocomplete is getting polluted over time by old/stale topics.
improve this situation/find solution.

visual bug: email VCS column has no "hovered" effect when the mouse overlaps the row.
why?

bug: remerge-diff VCS icon toString is problematic:
example: Add Pixel 10 Series Skins. stevenjenkins@google.com 11/3/25, 3:18 AM 3 notes Compare selected a89a6dc2 vs original 26503c02 cherry-pick (range-diff).

task: "add note" and "edit note" windows need keyboard-only controls (eg alt+S should save)

task: "edit note" window should have clickable links like `CONFLICT (content): community/android/project-system-gradle/src/com/android/tools/idea/projectsystem/gradle/sync/AndroidModuleDataService.kt` for easy navigation.
alt+left click maybe?

task to consider: how note changes over time? all note changes are materialized as commits under the hood.
so in theory, we should be able to show versions (content diff?).

task: when cherry pick could not be verified, include reason.

task: clicking empty space in VCS where notes icons are placed should open "new note" dialog

task: add demo project. it's getting more important over time because monorepo is too heavy and runIde requires reindexing. 
