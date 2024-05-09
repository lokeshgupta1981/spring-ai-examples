package com.howtodoinjava.ai.demo.web;


import org.springframework.ai.image.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiImageController {

  private final ImageClient imageClient;

  public OpenAiImageController(ImageClient imageClient) {
    this.imageClient = imageClient;
  }

  @GetMapping("/image-gen")
  public String imageGen(@RequestParam String message) {

    ImageOptions options = ImageOptionsBuilder.builder()
        .withModel("dall-e-3")
        .withHeight(1024)
        .withWidth(1024)
        .build();

    ImagePrompt imagePrompt = new ImagePrompt(message, options);
    ImageResponse response = imageClient.call(imagePrompt);
    String imageUrl = response.getResult().getOutput().getUrl();

    return "redirect:" + imageUrl;
  }
}
