package com.howtodoinjava.orderdesk.media;

import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import reactor.core.publisher.Flux;

@RestController
public class SpeechController {

    private final TextToSpeechModel speechModel;

    SpeechController(TextToSpeechModel speechModel) {
        this.speechModel = speechModel;
    }

    // Reads a support reply aloud and returns an MP3 file
    @GetMapping(value = "/speech", produces = "audio/mpeg")
    byte[] speak(@RequestParam String text) {
        return speechModel.call(text);
    }

    // The same with options for one request: another voice, slower, with a tone
    @GetMapping(value = "/speech/calm", produces = "audio/mpeg")
    byte[] speakCalm(@RequestParam String text) {
        TextToSpeechResponse response = speechModel.call(new TextToSpeechPrompt(text,
            OpenAiAudioSpeechOptions.builder()
                .voice(OpenAiAudioSpeechOptions.Voice.NOVA)
                .speed(0.9)
                .instructions("Speak in a calm, friendly customer support tone.")
                .build()));
        return response.getResult().getOutput();
    }

    // Writes each piece of audio to the response as soon as it arrives
    @GetMapping("/speech/stream")
    ResponseEntity<StreamingResponseBody> stream(@RequestParam String text) {
        Flux<byte[]> pieces = speechModel.stream(text);
        StreamingResponseBody body = out -> {
            for (byte[] piece : pieces.toIterable()) {
                out.write(piece);
                out.flush();
            }
        };
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("audio/mpeg"))
            .body(body);
    }
}
