package io.cloudNativeData.showcase.streaming.source.generator;

import nyla.solutions.core.io.csv.CsvReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class CsvConfig {

    @Value("${generator.activities.csv.file:classpath:csv/activities.csv}")
    private Resource filePath;

    @Bean
    CsvReader csvReader() throws IOException {
        return new CsvReader(new StringReader(filePath.getContentAsString(StandardCharsets.UTF_8)));
    }
}
