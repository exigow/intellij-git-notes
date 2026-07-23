# Development Tips

## IntelliJ Platform SDK

The IntelliJ Platform SDK documentation is the most up-to-date and authoritative reference for APIs, extension points, and plugin development concepts (such as the threading model, VFS, and more).
The entire documentation is available as a [single plaintext file](https://plugins.jetbrains.com/docs/intellij/llms.txt).

## IntelliJ MCP Server

The MCP settings expose a set of tools that allow you to interact with the running IDE.
You can use the **debugger**, analyze code, modify files, run configurations, or execute commands.

The entire IntelliJ Platform sources JAR is cached and indexed by JetBrains MCP.
To read platform code, locate a symbol with `search_symbol` (using `include_external=true`) and open with `read_file`.
