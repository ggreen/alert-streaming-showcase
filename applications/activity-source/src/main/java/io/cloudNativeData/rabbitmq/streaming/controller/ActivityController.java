package io.cloudNativeData.rabbitmq.streaming.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

@RestController
@RequestMapping("activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final Publisher<Activity> publisher;

    @PostMapping("activity")
    public void publish(@RequestBody Activity activity) {
        log.info("Publishing activity: {}", activity);

        publisher.send(activity);
    }
}
