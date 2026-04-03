package showcase.alarm;

import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.ArrayList;

@Configuration
public class RepositoryConfig {

    @Value("${repository.list.batch.size:10}")
    private int listBatchSize;

    @Bean
    ListRepository<Alert> listAlertRepository()
    {
        return new ListRepository<>(new ArrayList<>(listBatchSize),true);
    }

    @Bean
    ListRepository<Activity> listActivityRepository()
    {
        return new ListRepository<>(new ArrayList<>(listBatchSize),true);
    }
}
