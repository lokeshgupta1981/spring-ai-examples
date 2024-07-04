package com.howtodoinjava.ai.image;


import org.springframework.ai.image.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiImageController {

  private final ImageModel imageModel;

  public OpenAiImageController(ImageModel imageModel) {
    this.imageModel = imageModel;
  }

  @GetMapping("/image-gen")
  public String imageGen(@RequestParam String message) {

    ImageOptions options = ImageOptionsBuilder.builder()
        .withModel("dall-e-3")
        .withHeight(1024)
        .withWidth(1024)
        .build();

    ImagePrompt imagePrompt = new ImagePrompt(message, options);
    ImageResponse response = imageModel.call(imagePrompt);
    String imageUrl = response.getResult().getOutput().getUrl();

    return "redirect:" + imageUrl;
  }
}
