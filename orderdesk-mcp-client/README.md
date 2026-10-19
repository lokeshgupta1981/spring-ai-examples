# OrderDesk MCP Client

A small Spring Boot app that uses the tools of the OrderDesk MCP server.

1. Start Ollama with `qwen2.5:3b`, then start OrderDesk (it serves MCP at `http://localhost:8080/mcp`).
2. Start this app: `mvn spring-boot:run` (port 8081).
3. `curl localhost:8081/tools` lists the remote tools.
4. `curl "localhost:8081/ask?question=What%20is%20the%20status%20of%20order%20ORD-1042%3F"`
