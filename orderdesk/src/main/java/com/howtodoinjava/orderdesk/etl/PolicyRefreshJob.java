package com.howtodoinjava.orderdesk.etl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

// Loads the policy files at startup and then every hour.
// Turned on with orderdesk.ingest.schedule=true
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "orderdesk.ingest.schedule", havingValue = "true")
public class PolicyRefreshJob {

    private final PolicyIngestion policyIngestion;

    PolicyRefreshJob(PolicyIngestion policyIngestion) {
        this.policyIngestion = policyIngestion;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    void refresh() throws IOException {
        policyIngestion.ingest();
    }
}
