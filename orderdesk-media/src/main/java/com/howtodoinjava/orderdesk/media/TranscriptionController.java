package com.howtodoinjava.orderdesk.media;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openai.models.audio.AudioResponseFormat;

@RestController
public class TranscriptionController {

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;

    TranscriptionController(TranscriptionModel transcriptionModel, ChatClient.Builder builder) {
        this.transcriptionModel = transcriptionModel;
        this.chatClient = builder
            .defaultSystem("You are OrderDesk, the support assistant of an online store. "
                + "Answer in at most three sentences.")
            .build();
    }

    // Turns a customer's voice note into text
    @PostMapping("/transcribe")
    String transcribe(@RequestParam("audio") MultipartFile audio) {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audio.getResource(),
            OpenAiAudioTranscriptionOptions.builder()
                .language("en")
                .prompt("OrderDesk, order ids such as ORD-1042, INR")
                .responseFormat(AudioResponseFormat.TEXT)
                .temperature(0f)
                .build());
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        return response.getResult().getOutput();
    }

    // Voice note in, written support answer out
    @PostMapping("/voice-question")
    String voiceQuestion(@RequestParam("audio") MultipartFile audio) {
        String question = transcribe(audio);
        return chatClient.prompt().user(question).call().content();
    }
}
