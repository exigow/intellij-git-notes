Never commit/push unless explicitly asked.

## IntelliJ Platform SDK

The "platform SDK" is the most up-to-date reference for APIs, EPs, and plugin development fundamentals (e.g. disposing, threading, VFS).

The whole documentation are available as a single [file](https://plugins.jetbrains.com/docs/intellij/llms.txt).

## IntelliJ MCP Server

You can interact with the running IDE.
Use the debugger, analyze code, modify files, run configurations, or execute commands.

The platform sources are cached and indexed by JetBrains MCP.
To read those sources, use `search_symbol` with `include_external=true`, then open with `read_file`.

Before considering the task complete, use `lint_files` to check for issues.

## Semantic Versioning

Follow `major.minor.patch` versioning.

Always update the version in the same commit as the changes.
