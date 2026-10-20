package com.howtodoinjava.orderdesk.media;

import org.springframework.ai.moderation.ModerationModel;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.moderation.ModerationResponse;
import org.springframework.ai.moderation.ModerationResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModerationController {

    private final ModerationModel moderationModel;

    ModerationController(ModerationModel moderationModel) {
        this.moderationModel = moderationModel;
    }

    // Returns flagged, the categories and a score from 0 to 1 for each category
    @GetMapping("/moderate")
    ModerationResult moderate(@RequestParam String text) {
        ModerationResponse response = moderationModel.call(new ModerationPrompt(text));
        return response.getResult().getOutput().getResults().get(0);
    }
}
