package com.howtodoinjava.ai.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ImageGenController {

  private final ImageGenService imageGenService;

  public ImageGenController(ImageGenService imageGenService) {
    this.imageGenService = imageGenService;
  }

  @PostMapping("/generate-image")
  public String imageGen(@RequestBody ImageGenRequest request) {

    String imageUrl = imageGenService.imageGen(request);
    return "redirect:" + imageUrl;
  }
}
