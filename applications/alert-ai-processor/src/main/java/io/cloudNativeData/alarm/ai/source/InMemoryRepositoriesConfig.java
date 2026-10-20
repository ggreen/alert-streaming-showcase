package io.cloudNativeData.alarm.ai.source;

import nyla.solutions.core.data.collections.CapacityList;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.cloudNativeData.streaming.showcase.domains.Activity;

@Configuration
public class InMemoryRepositoriesConfig {

    @Value("${app.memory.max.capacity.count:400}")
    private int maxCapacityCount;

    @Bean
    ListRepository<Activity> activitiesRepository()
    {
        return new ListRepository<>(new CapacityList<>(maxCapacityCount));
    }
}
