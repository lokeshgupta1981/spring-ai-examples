package com.howtodoinjava.supportdesk;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.domain.io.UserInput;
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
    CommandLineRunner askAboutTicket(AgentPlatform agentPlatform) {
        return args -> {
            SupportReply reply = AgentInvocation.builder(agentPlatform)
                    .build(SupportReply.class)
                    .invoke(new UserInput("What is the status of ticket T-1001?"));
            System.out.println(reply.message());
        };
    }
}
