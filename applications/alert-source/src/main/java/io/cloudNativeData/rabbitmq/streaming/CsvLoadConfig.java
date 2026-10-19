package io.cloudNativeData.rabbitmq.streaming;

import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.csv.CsvReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import showcase.streaming.domains.Alert;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class CsvLoadConfig {

    //classpath:csv/alerts.csv
    @Value("${csv.file:}")
    private Resource resource;

    @Bean
    Iterator<List<String>> csvLines() throws IOException {

        // Safe check for null or non-existent resource
        if (resource == null || !resource.exists()) {
            log.warn("CSV resource is empty or not found. Skipping load.");
            return (Iterator)Collections.emptyList().iterator();
        }

        return new CsvReader(resource.getFile()).stream().iterator();
    }

}
