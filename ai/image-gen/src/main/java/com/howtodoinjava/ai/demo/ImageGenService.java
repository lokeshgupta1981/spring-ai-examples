package com.howtodoinjava.ai.demo;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

@Service
public class ImageGenService {

  private final ImageModel imageModel;

  public ImageGenService(ImageModel imageModel) {
    this.imageModel = imageModel;
  }

  public String imageGen(ImageGenRequest request) {
    ImageOptions options = ImageOptionsBuilder.builder().withModel("dall-e-3").build();

    ImagePrompt imagePrompt = new ImagePrompt(request.prompt(), options);
    ImageResponse response = imageModel.call(imagePrompt);
    String imageUrl = response.getResult().getOutput().getUrl();
    return imageUrl;
  }
}
