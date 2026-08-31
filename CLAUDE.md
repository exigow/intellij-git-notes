# Development Tips

## IntelliJ Platform SDK

The "platform" is the most up-to-date reference for APIs, EPs, and plugin development fundamentals (e.g. disposing, threading, VFS).

Docs are available as a single [file](https://plugins.jetbrains.com/docs/intellij/llms.txt).

## IntelliJ MCP Server

You to interact with the running IDE.
Use the debugger, analyze code, modify files, run configurations, or execute commands.

The entire IntelliJ Platform sources JAR is cached and indexed by JetBrains MCP.
To read platform code, locate a symbol with `seasrch_symbol` (using `include_external=true`) and open with `read_file`.

Before considering the task complete, use `lint_files` to check for issues.
