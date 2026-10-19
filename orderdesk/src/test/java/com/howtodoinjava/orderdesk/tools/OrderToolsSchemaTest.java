package com.howtodoinjava.orderdesk.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.support.ToolCallbacks;

class OrderToolsSchemaTest {

    @Test
    void toolsBecomeJsonSchemas() {
        ToolCallback[] callbacks = ToolCallbacks.from(new OrderTools(null));
        for (ToolCallback callback : callbacks) {
            System.out.println(callback.getToolDefinition().name() + " "
                + callback.getToolDefinition().inputSchema());
        }
        assertThat(callbacks).hasSize(3);
    }
}
