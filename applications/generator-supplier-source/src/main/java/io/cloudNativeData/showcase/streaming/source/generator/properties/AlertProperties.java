package io.cloudNativeData.showcase.streaming.source.generator.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "generator.alert")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertProperties {
    private int idStartSequence;
    private List<String> levels;
    private String account;
    private List<String> events;

}
