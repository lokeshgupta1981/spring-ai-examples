package com.howtodoinjava.supportdesk;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SupportDeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportDeskApplication.class, args);
    }

    @Bean
    CommandLineRunner askAboutTicket(ChatClient.Builder builder) {
        return args -> {
            String answer = builder.build()
                    .prompt("What is the status of ticket T-1001?")
                    .tools(new SupportTools())
                    .call()
                    .content();
            System.out.println(answer);
        };
    }
}
