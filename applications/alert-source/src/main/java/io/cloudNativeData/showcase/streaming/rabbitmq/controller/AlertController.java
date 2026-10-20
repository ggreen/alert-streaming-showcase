package io.cloudNativeData.showcase.streaming.rabbitmq.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.cloudNativeData.streaming.showcase.domains.Alert;

@RestController
@RequestMapping("alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final Publisher<Alert> publisher;

    @PostMapping("alert")
    public void publish(@RequestBody Alert alert) {
        log.info("Publishing alert: {}", alert);

        publisher.send(alert);
    }
}
