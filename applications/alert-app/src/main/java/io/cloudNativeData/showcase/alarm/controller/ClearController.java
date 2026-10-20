package io.cloudNativeData.showcase.alarm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.cloudNativeData.streaming.showcase.domains.Activity;
import io.cloudNativeData.streaming.showcase.domains.Alert;

@RestController
@RequestMapping("clear")
@RequiredArgsConstructor
@Slf4j
public class ClearController {
    private final ListRepository<Activity> activityRepository;
    private final ListRepository<Alert> alerrtRepository;

    @DeleteMapping
    public void clear() {

        log.info("Clearing all activities ");
        activityRepository.deleteAll();

        log.info("Clearing all alerts ");
        alerrtRepository.deleteAll();
    }
}
