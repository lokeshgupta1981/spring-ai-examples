package com.howtodoinjava.ai.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

  static Logger LOG = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Autowired
  OpenAiChatController chatController;

  @Override
  public void run(String... args) throws Exception {

    String response = chatController.tellSimpleJoke().get("generation");
    LOG.info(response);
  }
}
