# Multi-Agent AI in Java (LangChain4j agentic)

Code for the howtodoinjava.com article "Multi-Agent AI in Java: Patterns and a LangChain4j Example".

A support desk built from five agents: a triage agent routes each ticket to a billing or shipping agent, and a reviewer and reviser check the reply in a loop. A single-agent baseline answers the same tickets for comparison.

## Requirements

- Java 21, Maven 3.9
- Ollama with the model pulled: `ollama pull qwen2.5:7b`

## Run

```
mvn compile
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp target/classes:$(cat cp.txt) com.howtodoinjava.supportdesk.SupportDeskApp multi
java -cp target/classes:$(cat cp.txt) com.howtodoinjava.supportdesk.SupportDeskApp single
java -cp target/classes:$(cat cp.txt) com.howtodoinjava.supportdesk.SupervisorDemo
```

Each ticket prints one line: mode, ticket id, expected and chosen department, model calls, tokens, milliseconds and the reply. `measure.sh` runs both modes three times and `analyze.py runs/results.txt` prints the medians.

Versions: LangChain4j 1.22.0, langchain4j-agentic 1.22.0-beta32 (experimental module).
