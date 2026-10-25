package com.howtodoinjava.supportdesk;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;

@Agent(description = "Answers questions about support tickets")
public class SupportDeskAgent {

    @AchievesGoal(description = "The customer's ticket question is answered")
    @Action
    public SupportReply answer(UserInput input, OperationContext context) {
        return context.ai()
                .withDefaultLlm()
                .withToolObject(new SupportTools())
                .createObject("Answer the customer: " + input.getContent(), SupportReply.class);
    }
}
