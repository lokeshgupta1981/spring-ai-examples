package com.howtodoinjava.ai.demo;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.StreamingSpeechModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin
class SpeechController {

  private final OpenAiAudioSpeechModel speechModel;

  SpeechController(OpenAiAudioSpeechModel speechModel) {
    this.speechModel = speechModel;
  }

  @PostMapping("/speech")
  byte[] speech(@RequestBody String message) {

    return speechModel.call(new SpeechPrompt(message))
      .getResult()
      .getOutput();
  }

  @GetMapping("/stream-speech")
  Flux<byte[]> streamingSpeech(@RequestParam String message) {

    OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
      .withResponseFormat(AudioResponseFormat.OPUS)
      .build();

    OpenAiAudioApi openAiAudioApi = new OpenAiAudioApi(System.getenv("OPENAI_API_KEY"));
    StreamingSpeechModel streamingSpeechModel = new OpenAiAudioSpeechModel(openAiAudioApi, speechOptions);

    return streamingSpeechModel.stream(message);
  }

  @PostMapping("/speech_v2")
  byte[] speechWithOpenAiOptions(@RequestBody String message) {

    /*OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
      .withModel(OpenAiAudioApi.TtsModel.TTS_1.getValue())
      .withResponseFormat(AudioResponseFormat.MP3)
      .withVoice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
      .withSpeed(1.0f)
      .build();

    var openAiAudioApi = new OpenAiAudioApi(System.getenv("OPENAI_API_KEY"));
    SpeechModel openAiAudioSpeechModel = new OpenAiAudioSpeechModel(openAiAudioApi, speechOptions);*/

    var speechResponse = speechModel
      .call(new SpeechPrompt(message, OpenAiAudioSpeechOptions.builder()
        .withModel("tts-1")
        .withVoice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
        .withResponseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
        .withSpeed(1.0f)
        .build()));
    return speechResponse.getResult().getOutput();
  }
}
