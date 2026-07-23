# Git Notes

This minimal plugin adds support for [git-notes](https://git-scm.com/docs/git-notes) in IntelliJ IDEA.

## Quick Start

* `./gradlew runIde` - run IDE with installed plugin
* `./gradlew buildPlugin` - build plugin installer (outputs `build/distributions/intellij-git-notes.zip`)
* `./gradlew verifyPlugin` - a compatibility check against different IDE versions (takes time)

## TODO

* bug: can't create new empty note (!)
* add single good e2e test that goes through all features
* bug: ctrl+c on commit puts internal class name into copied string (missing "pretty" toString impl?)
* find solution for stale topics (I think they should not be present in autocompletion)
* add "prune stale topics" action (?)
