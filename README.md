# Git Notes

This minimal plugin adds support for [git-notes](https://git-scm.com/docs/git-notes) in IntelliJ IDEA.

## Quick Start

* `./gradlew runIde` - run IDE with installed plugin
* `./gradlew buildPlugin` - build plugin installer (outputs `build/distributions/intellij-git-notes.zip`)
* `./gradlew verifyPlugin` - a compatibility check against different IDE versions (takes time)
* `./gradlew publishPlugin -Ptoken=TOKEN` - upload new version to the JetBrains Marketplace

## Screenshots

![image](docs/enable-columns.png)

![image](docs/edit-notes.png)

## TODO

* bug: can't create new empty note
* task: add single good e2e test that goes through all features
* bug: autocompleted topics are getting polluted by old/stale topics -- maybe hide those options? how to identify such?
* bug: e-mail VCS row has no "hovered" effect when mouse overlaps the row -- maybe because custom implementations?
