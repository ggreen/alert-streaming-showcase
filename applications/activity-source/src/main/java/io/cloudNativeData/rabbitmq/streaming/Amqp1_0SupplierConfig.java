package io.cloudNativeData.rabbitmq.streaming;

import com.rabbitmq.client.amqp.Connection;
import com.rabbitmq.client.amqp.Environment;
import com.rabbitmq.client.amqp.Management;
import com.rabbitmq.client.amqp.impl.AmqpEnvironmentBuilder;
import io.cloudNativeData.rabbitmq.streaming.constants.MessagingConstants;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import showcase.streaming.domains.Activity;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
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

    @Value("${spring.cloud.stream.bindings.output.destination}")
    private String streamName;

    @Value("${spring.cloud.stream.default.contentType:application/json}")
    private String contentType;


    @Value("${source.amqp.filter.property.name:account}")
    private String filterProperty;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    @Value("${spring.rabbitmq.ssl.enabled:false}")
    private boolean sslEnabled;


    @Bean
    com.rabbitmq.client.amqp.Publisher amqpPublisher(Connection connection) {
        return  connection
                .publisherBuilder()
                .queue(streamName)
                .build();
    }

    @Bean("publisherMsgChannel")
    MessageChannel publisher(Connection connection,
                             com.rabbitmq.client.amqp.Publisher amqpPublisher)
    {


        return (message,timeout) -> {
            String body = (String)message.getPayload();
            String filterValue = message.getHeaders().get(MessagingConstants.TOPIC_HEADER, String.class);
            var msg = amqpPublisher
                    .message(body.getBytes(StandardCharsets.UTF_8))
                    .annotation(MessagingConstants.FILTER_VALUE_NM, filterValue)
                    .contentType(contentType)
                    .property(filterProperty, filterValue);

            amqpPublisher.publish(msg, context -> {});

            log.info("published {}:{} body:{}", filterProperty,filterValue,body);
            return true;
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
    Publisher<Activity> alertPublisher(Connection connection,
                                       com.rabbitmq.client.amqp.Publisher amqpPublisher,

                                       JsonMapper jsonMapper) {

        return (activity) -> {

            var msg = amqpPublisher
                    .message(jsonMapper.writeValueAsString(activity)
                            .getBytes(StandardCharsets.UTF_8))
                    .annotation(MessagingConstants.FILTER_VALUE_NM, activity.account())
                    .contentType(contentType)
                    .property("account", activity.account())
                    .property("icon", activity.icon());

            amqpPublisher.publish(msg, context -> {});

            log.info("Published: {}", activity);
        };
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
