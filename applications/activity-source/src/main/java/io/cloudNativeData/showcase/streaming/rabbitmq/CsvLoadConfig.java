package io.cloudNativeData.showcase.streaming.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.csv.CsvReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Configuration
@Slf4j
public class CsvLoadConfig {

    @Value("${csv.file:classpath:csv/activities.csv}")
    private Resource resource;

    @Bean
    Iterator<List<String>> csvLines() throws IOException {
        return new CsvReader(resource.getFile()).stream().iterator();
    }

}
