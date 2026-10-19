package com.howtodoinjava.orderdesk.etl;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestController {

    private final PolicyIngestion policyIngestion;

    IngestController(PolicyIngestion policyIngestion) {
        this.policyIngestion = policyIngestion;
    }

    @PostMapping("/ingest")
    PolicyIngestion.IngestResult ingest() throws IOException {
        return policyIngestion.ingest();
    }
}
