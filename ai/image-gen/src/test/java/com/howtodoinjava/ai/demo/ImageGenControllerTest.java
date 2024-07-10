package com.howtodoinjava.ai.demo;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ImageGenController.class)
class ImageGenControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ImageGenService imageGenService;

  @Test
  void testImageGen() throws Exception {

    String prompt = "test-prompt";
    String imageUrl = "test-url";
    ImageGenRequest request = new ImageGenRequest(prompt);
    when(imageGenService.imageGen(request)).thenReturn(imageUrl);

    mockMvc.perform(post("/generate-image")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"prompt\": \"test-prompt\"}"))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("test-url"));
  }
}