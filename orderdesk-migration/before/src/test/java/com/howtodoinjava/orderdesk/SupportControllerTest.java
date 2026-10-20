package com.howtodoinjava.orderdesk;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupportController.class)
class SupportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    ChatClient chatClient;

    @Test
    void askReturnsTheModelAnswer() throws Exception {
        when(chatClient.prompt().user(any(String.class)).advisors(any(java.util.function.Consumer.class))
            .options(any()).call().content()).thenReturn("It ships tomorrow.");

        mockMvc.perform(get("/ask").param("customer", "priya").param("question", "Where is ORD-1043?"))
            .andExpect(content().string("It ships tomorrow."));
    }
}
