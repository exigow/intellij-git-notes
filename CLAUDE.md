# Git Notes

## Useful Resources

### IntelliJ Platform SDK

Those docs are the most up-to-date, authoritative reference for APIs, extension points, and plugin development concepts (like threading model, VFS and more).
The entire SDK docs are available as [one plaintext file](https://plugins.jetbrains.com/docs/intellij/llms.txt).

### IntelliJ MCP Server

This MCP exposes a set of tools that allow you to interact with the running IDE.
You can use **debugger**, analyze code, modify files, run configurations, or execute commands.

The entire IntelliJ Platform sources JAR is cached and indexed by JetBrains MCP.
To read platform code, locate a symbol with `search_symbol` (with `include_external=true`) and open the returned JAR path with `read_file`.

## Code Style

* No comments/Javadocs.
* One top-level class per file, with the file named after the class.