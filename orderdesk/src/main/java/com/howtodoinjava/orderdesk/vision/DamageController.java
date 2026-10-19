package com.howtodoinjava.orderdesk.vision;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DamageController {

    private final ChatClient visionClient;

    DamageController(@Qualifier("visionClient") ChatClient visionClient) {
        this.visionClient = visionClient;
    }

    // Step 1: an image from the classpath and a question about it
    @GetMapping("/damage/sample")
    String describeSample() {
        var photo = new ClassPathResource("images/damaged-parcel.jpg");
        return visionClient.prompt()
            .user(u -> u.text("Describe this parcel in two sentences. Is it damaged?")
                        .media(MimeTypeUtils.IMAGE_JPEG, photo))
            .call()
            .content();
    }

    // Step 2: an image that the customer uploads
    @PostMapping("/damage/describe")
    String describe(@RequestParam("photo") MultipartFile photo) {
        return visionClient.prompt()
            .user(u -> u.text("Describe this parcel in two sentences. Is it damaged?")
                        .media(MimeType.valueOf(photo.getContentType()), photo.getResource()))
            .call()
            .content();
    }

    // Step 3: the same upload, returned as a Java record
    @PostMapping("/damage/report")
    DamageReport report(@RequestParam("photo") MultipartFile photo) {
        return visionClient.prompt()
            .user(u -> u.text("""
                    Fill a damage report for this parcel. Read the order number
                    from the label. damageType is one of: none, crushed, torn, wet.""")
                        .media(MimeType.valueOf(photo.getContentType()), photo.getResource()))
            .call()
            .entity(DamageReport.class);
    }
}
