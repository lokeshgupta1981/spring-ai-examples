package com.howtodoinjava.ai.demo;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SqlController {

  @Value("classpath:/schema.sql")
  private Resource ddlResource;

  @Value("classpath:/sql-prompt-template.st")
  private Resource sqlPromptTemplateResource;

  private final ChatClient aiClient;
  private final JdbcTemplate jdbcTemplate;

  public SqlController(ChatClient.Builder aiClientBuilder, JdbcTemplate jdbcTemplate) {
    this.aiClient = aiClientBuilder.build();
    this.jdbcTemplate = jdbcTemplate;
  }

  @PostMapping(path = "/sql")
  public AiResponse sql(@RequestBody AiRequest request) throws IOException {
    String schema = ddlResource.getContentAsString(Charset.defaultCharset());

    String query = aiClient.prompt()
      .advisors(new SimpleLoggerAdvisor())
      .user(userSpec -> userSpec
        .text(sqlPromptTemplateResource)
        .param("question", request.text())
        .param("ddl", schema)
      )
      .call()
      .content();

    if (query.toLowerCase().startsWith("select")) {
      return new AiResponse(query, jdbcTemplate.queryForList(query));
    }
    throw new AiException(query);
  }
}
