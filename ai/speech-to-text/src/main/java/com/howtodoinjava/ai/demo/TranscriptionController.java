package com.howtodoinjava.ai.demo;

import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptResponseFormat;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TranscriptionController {

  private final OpenAiAudioTranscriptionModel transcriptionModel;

  TranscriptionController(OpenAiAudioTranscriptionModel transcriptionModel) {
    this.transcriptionModel = transcriptionModel;
  }

  @GetMapping("/transcription")
  String speech(@Value("classpath:speech.mp3") Resource audioFile) {
    return transcriptionModel.call(new AudioTranscriptionPrompt(audioFile))
      .getResult()
      .getOutput();
  }

  @GetMapping("/transcription_v2")
  String speech_v2(@Value("classpath:speech.mp3") Resource audioFile) {

    var transcriptionResponse = transcriptionModel
      .call(new AudioTranscriptionPrompt(audioFile, OpenAiAudioTranscriptionOptions.builder()
        .withLanguage("en")
        .withPrompt("Create transcription for this audio file.")
        .withTemperature(0f)
        .withResponseFormat(TranscriptResponseFormat.TEXT)
        .build()));
    return transcriptionResponse.getResult().getOutput();
  }
}