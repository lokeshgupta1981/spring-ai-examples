package com.howtodoinjava.orderdesk.media;

import java.util.Base64;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    private final ImageModel imageModel;

    ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    // Generates a banner for a store promotion and returns it as a PNG
    @GetMapping(value = "/images/banner", produces = MediaType.IMAGE_PNG_VALUE)
    byte[] banner(@RequestParam String product) {
        String text = """
            A clean, bright e-commerce banner for an online store in India.
            Product: %s. Show the product on a plain light background.
            Do not draw any text, logos or people.""".formatted(product);

        ImageResponse response = imageModel.call(new ImagePrompt(text,
            OpenAiImageOptions.builder()
                .size("1536x1024")
                .quality("low")
                .n(1)
                .build()));

        // gpt-image models return the picture as Base64, not as a URL
        String base64 = response.getResult().getOutput().getB64Json();
        return Base64.getDecoder().decode(base64);
    }
}
