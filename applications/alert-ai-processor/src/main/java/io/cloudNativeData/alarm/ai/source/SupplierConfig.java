package io.cloudNativeData.alarm.ai.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import io.cloudNativeData.alarm.ai.source.service.AlertDetectorService;
import showcase.streaming.domains.Alert;

import java.util.List;
import java.util.function.Supplier;

@Configuration
public class SupplierConfig {

    @Value("${alerts.header.names.account}")
    private String accountHeaderName;

    @Value("${alerts.header.names.level}")
    private String levelHeaderName;

    @Bean
    Supplier<List<Message<Alert>>> checkForAlerts(AlertDetectorService alertDetectorService){
        return () ->
        {
            var alerts = alertDetectorService.checkForAlerts();
            return alerts != null ? alerts.stream().map( alert ->
                    MessageBuilder.withPayload(alert)
                            .setHeader(accountHeaderName,alert.account())
                            .setHeader(levelHeaderName,alert.level())
                            .build()).toList() : null;
        };
    }
}
