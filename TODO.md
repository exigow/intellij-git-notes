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

task: conflict markers in the remerge diff break syntax highlighting.
the diff viewer highlights lexer-only, so an unbalanced quote/brace/comment inside one
conflict section leaks past the marker line and miscolours the rest of the file.
fix idea: in RemergeMarkerDiffExtension, also call EditorEx.setHighlighter() with a
SyntaxHighlighter wrapper whose getHighlightingLexer() lexes each conflict section
independently and emits marker lines as a dedicated token type.
