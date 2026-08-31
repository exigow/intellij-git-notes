# Git Notes

This minimal plugin adds support for [git-notes](https://git-scm.com/docs/git-notes) in IntelliJ IDEA.

## Quick Start

* `./gradlew runIde` - run IDE with installed plugin
* `./gradlew buildPlugin` - build plugin installer (outputs `build/distributions/intellij-git-notes.zip`)
* `./gradlew verifyPlugin` - a compatibility check against different IDE versions (takes time)
* `./gradlew publishPlugin -Ptoken=TOKEN` - upload new version to the JetBrains Marketplace

## TODO

* bug: can't create new empty note (!)
* add single good e2e test that goes through all features
* find solution for stale topics (I think they should not be present in autocompletion)
* add "prune stale topics" action (?)

### TODO: remerge-difftool 

i want to add a feature that produces similar, but improved view like: `android-merge merge remerge-difftool b233a23b9e46`
context: in ~/projects/ultimate repo i have number of commits that are authored by google/in AOSP but cherry-picked into our codebase
i want to introduce an action, that produces a IDE view that will allow me to overview to make informed decision: how cherry picked commit changed versus original change.
this will allow me to inspect such cherry picked commits and see the differences how our patches looks
i think this action should be only enabled for commits that have "cherry-picked from" footer with hash (because otherwise i would have not sense, right?)
as me a follow up questions to narrow down the specification
also: ~/projects/android-deps-build-config contains a feature that works similarly (see web ui, heimdall branch)




check WIP commit and continue working on it with new feature: introduce new vsc log status similar to notes icon.

statuses:
* no icon - when commit is not cherry picked / remerge-diff cannot be applied
* success icon - when commit is cherry picked and applied cleanly / remerge-diff cannot be applied
* warning icon - when commis is cherry picked, but not applied cleanly
* anything else? think. i need to have quick overview what commits need to be inspected (when remerge diff will show some interesting info)

also, i want to enable remerge-diff action only for warning case, because otherwise remerge diff has no value, right?

---

udostepniej ekran zeby pokazac wam mniej wiecej jak to reviewuje i dlaczego ten plugin mi sie bardzo przydal
jestem przygotowany wiec pokazal bym odrazu

---

panda 3 is ready but we are not happy yet with the results
we're no longer cherry-picking commits manually
instead, we iterate on the agent loop

---

