package io.cloudNativeData.showcase.streaming.rabbitmq;

import com.rabbitmq.client.amqp.Connection;
import com.rabbitmq.client.amqp.Environment;
import com.rabbitmq.client.amqp.Management;
import com.rabbitmq.client.amqp.impl.AmqpEnvironmentBuilder;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import io.cloudNativeData.streaming.showcase.domains.Alert;
import io.cloudNativeData.showcase.streaming.rabbitmq.constants.MessagingConstants;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
@Profile("amqp1.0")
public class Amqp1_0SupplierConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.rabbitmq.routing.key:}")
    private String routingKey;

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.application.name:http-source}")
    private String name;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${spring.cloud.stream.bindings.output.group}")
    private String streamName;

    @Value("${spring.cloud.stream.default.contentType:application/json}")
    private String contentType;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${source.amqp.filter.property.name:account}")
    private String filterProperty;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    @Value("${spring.rabbitmq.ssl.enabled:false}")
    private boolean sslEnabled;

    @Bean("publisher")
    MessageChannel publisher(Connection connection,
                             com.rabbitmq.client.amqp.Publisher amqpPublisher) {

        log.info("Creating publisher with filterProperty: {}", filterProperty);

        return (message, timeout) -> {
            String body = (String) message.getPayload();
            MessageHeaders headers = message.getHeaders();
            String filterValue = headers.get(MessagingConstants.TOPIC_HEADER, String.class);

            var msg = amqpPublisher
                    .message(body.getBytes(StandardCharsets.UTF_8))
                    .annotation(MessagingConstants.FILTER_VALUE_NM, filterValue)
                    .contentType(contentType)
                    .property(filterProperty, filterValue);

            // Map all custom Spring message headers into stream message properties
            headers.forEach((key, value) -> {
                // Exclude Spring internal headers (e.g., id, timestamp) if desired
                if (value != null && !MessageHeaders.ID.equals(key) && !MessageHeaders.TIMESTAMP.equals(key)) {

                    var msgPropertyName = key.toLowerCase();
                    log.info("Property name: {}, value: {}", msgPropertyName,value);
                    msg.property(msgPropertyName, value.toString());
                }
            });

            amqpPublisher.publish(msg, context -> {});

            log.info("Published filterProperty: {}, filterValue: {}, body: {}", filterProperty, filterValue,body);
            return true;
        };
    }

    @Bean
    com.rabbitmq.client.amqp.Publisher amqpPublisher(Connection connection) {
        return  connection
                        .publisherBuilder()
                        .queue(streamName)
                        .build();
    }

    @Bean
    Publisher<Alert> alertPublisher(Connection connection,
                                    com.rabbitmq.client.amqp.Publisher amqpPublisher,

                               JsonMapper jsonMapper) {

            return (alert) -> {

                var msg = amqpPublisher
                        .message(jsonMapper.writeValueAsString(alert)
                                .getBytes(StandardCharsets.UTF_8))
                        .annotation(MessagingConstants.FILTER_VALUE_NM, alert.account())
                        .contentType(contentType)
                        .property("account", alert.account())
                        .property("level", alert.level());

                amqpPublisher.publish(msg, context -> {});

                log.info("Published: {}", alert);
            };
    }

    @Bean
    Environment amqpEnvironment()
    {
        return new AmqpEnvironmentBuilder()
                .build();
    }

    @Bean
    Connection amqpConnection(Environment environment)
    {
        var builder = environment.connectionBuilder().host(host)
                .host(host)
                .port(port)
                .name(name)
                .username(username)
                .password(password)
                .virtualHost(virtualHost);


        if (sslEnabled) {
            builder.tls();
        }

        return builder.build();
    }

    @Bean
    Management amqpManagement(Connection connection)
    {
        return connection.management();
    }

    @ConditionalOnProperty(
            name = "spring.cloud.stream.bindings.output.destination",
            matchIfMissing = true,
            havingValue = "!amq.topic" // Note: Expressing "NOT" directly inside havingValue is unsupported
    )
    @Bean
    Management.QueueInfo messageAmqpStream(Management management)
    {

        return management
                .queue()
                .name(streamName)
                .stream()
                .queue()
                .declare();
    }
}
