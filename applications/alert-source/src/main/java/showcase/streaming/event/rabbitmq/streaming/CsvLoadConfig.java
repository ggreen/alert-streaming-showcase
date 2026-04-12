package showcase.streaming.event.rabbitmq.streaming;

import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.csv.CsvReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import showcase.streaming.domains.Alert;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class CsvLoadConfig {

    @Value("classpath:csv/alerts.csv")
    private Resource resource;

    @Bean
    Iterator<List<String>> csvLines() throws IOException {
        return new CsvReader(resource.getFile()).stream().iterator();
    }


    @Bean
    Supplier<Alert> alerts(Iterator<List<String>> csvLines) {

        return () -> {
            if(csvLines.hasNext()) {
                var line = csvLines.next();
                log.info("Events {}",line);
                return  Alert.builder()
                        .id(line.get(0))
                        .account(line.get(1))
                        .level(line.get(2))
                        .time(line.get(3))
                        .event(line.get(4))
                        .build();
            }
            return null;
        };
    }
}
