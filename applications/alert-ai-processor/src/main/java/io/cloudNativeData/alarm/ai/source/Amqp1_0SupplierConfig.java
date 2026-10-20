package io.cloudNativeData.alarm.ai.source;

import com.rabbitmq.client.amqp.Connection;
import com.rabbitmq.client.amqp.Consumer;
import com.rabbitmq.client.amqp.Environment;
import com.rabbitmq.client.amqp.impl.AmqpEnvironmentBuilder;
import io.cloudNativeData.alarm.ai.source.constants.MessagingConstants;
import io.cloudNativeData.alarm.ai.source.functions.AlertAiProcessor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import io.cloudNativeData.streaming.showcase.domains.Activity;
import io.cloudNativeData.streaming.showcase.domains.Alert;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class Amqp1_0SupplierConfig {

    @Value("${spring.application.name}")
    private String applicationName;


    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.application.name:http-source}")
    private String name;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${spring.cloud.stream.bindings.output.destination:activities.activity}")
    private String inputStream;

    @Value("${spring.cloud.stream.bindings.output.destination:alerts.alert}")
    private String outputStream;

    @Value("${spring.cloud.stream.default.contentType:application/json}")
    private String contentType;


    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    @Value("${spring.rabbitmq.ssl.enabled:false}")
    private boolean sslEnabled;

    @Value("${stream.activity.filter.value}")
    private String filterValue;

    @Value("${stream.activity.filter.name:account}")
    private String filterPropName;

    @Bean
    com.rabbitmq.client.amqp.Publisher amqpPublisher(Connection connection) {
        return  connection
                .publisherBuilder()
                .queue(outputStream)
                .build();
    }

    @Bean("publisherMsgChannel")
    Publisher<Message<Alert>> publisher(Connection connection,
                             com.rabbitmq.client.amqp.Publisher amqpPublisher, JsonMapper jsonMapper)
    {

        return message -> {
            var alert = message.getPayload();
            var msg = amqpPublisher
                    .message(jsonMapper.writeValueAsString(alert).getBytes(StandardCharsets.UTF_8))
                    .annotation(MessagingConstants.FILTER_VALUE_NM, alert.account())
                    .contentType(contentType)
                    .property("account", alert.account())
                    .property("level", alert.level());

            amqpPublisher.publish(msg, context -> {});

            log.info("published alert:{} to stream: {}", alert,outputStream);
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
    Consumer amqpConsumer(Connection connection, AlertAiProcessor processor ,
                          JsonMapper jsonMapper,
                          Publisher<Message<Alert>> publisher)
    {
        // do not forget to build the instance!

        log.info("Consuming from input stream: {}, filterValue: {}", inputStream, filterValue  );

        return connection.consumerBuilder().
                queue(inputStream)
                .stream()
                .filterValues(filterValue)
                .filterMatchUnfiltered(true)
                .builder()
                .messageHandler((context, message) -> {

                    var msgFilterValue = (String) message.annotation("x-stream-filter-value");
                    // there must be some client-side filter logic
                    if (!filterValue.equals(msgFilterValue) ) {
                        log.info("Filter msgFilterValue:{}",msgFilterValue);
                        return;
                        // message processing
                    }

                    byte[] body = message.body();
                    Activity activity = jsonMapper.readValue(body, Activity.class);
                    log.info("receive activity:{}",activity);

                    var listOfAlerts = processor.apply(activity);

                    if(listOfAlerts != null && !listOfAlerts.isEmpty()) {
                        for(var alert: listOfAlerts) {
                            publisher.send(alert);
                        }
                    }

                    context.accept(); // settle the message
                })
                .build();
    }
}
