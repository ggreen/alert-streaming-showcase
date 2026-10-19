package io.cloudNativeData.alarm.ai.source;

import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.streaming.domains.Activity;

import java.util.ArrayList;

@Configuration
public class InMemoryRepositoriesConfig {

    @Bean
    ListRepository<Activity> activitiesRepository()
    {
        return new ListRepository<>(new ArrayList<>());
    }
}
