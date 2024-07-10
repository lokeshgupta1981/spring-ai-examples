package com.howtodoinjava.ai.demo.demo;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(QueryController.class)
public class QueryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private QueryService queryService;

  @MockBean
  private ChatClient.Builder builder;

  @Test
  public void testQuery() throws Exception {

    String subject = "testSubject";
    String explanation = "testExplanation";
    QueryResponse mockResponse = new QueryResponse("Explanation for : " + subject, explanation);
    when(queryService.query(anyString())).thenReturn(mockResponse);

    mockMvc.perform(get("/explain")
        .param("subject", subject))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.explanation").value(mockResponse.explanation()));
  }
}
