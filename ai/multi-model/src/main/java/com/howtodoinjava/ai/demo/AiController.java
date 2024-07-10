package com.howtodoinjava.ai.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AiController {

  private final ChatClient chatClient;

  public AiController(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  @PostMapping("/ask")
  public Response ask(@RequestParam("image") MultipartFile imageFile,
    @RequestBody Request request) throws Exception {

    ByteArrayResource imageResource = new ByteArrayResource(imageFile.getBytes());

    var answer = chatClient.prompt()
      .user(userSpec -> userSpec
        .text(request.query())
        .media(MimeTypeUtils.IMAGE_JPEG, imageResource))
      .call()
      .content();
    return new Response(answer);
  }
}
