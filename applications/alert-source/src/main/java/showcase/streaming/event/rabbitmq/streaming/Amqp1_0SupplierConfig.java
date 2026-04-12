package showcase.streaming.event.rabbitmq.streaming;

import com.rabbitmq.client.amqp.Connection;
import com.rabbitmq.client.amqp.Environment;
import com.rabbitmq.client.amqp.Management;
import com.rabbitmq.client.amqp.impl.AmqpEnvironmentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageChannel;
import showcase.streaming.event.rabbitmq.streaming.constants.MessagingConstants;

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

    @Value("${spring.cloud.stream.bindings.output.destination}")
    private String streamName;

    @Value("${spring.cloud.stream.default.contentType:application/json}")
    private String contentType;


    @Value("${source.amqp.filter.property.name}")
    private String filterProperty;


    @Bean("publisher")
    MessageChannel publisher(Connection connection, Management.QueueInfo streamInfo)
    {
        var publisher =
                connection
                        .publisherBuilder()
                        .queue(streamInfo.name())
                        .build();

        return (message,timeout) -> {
            String body = (String)message.getPayload();
            String filterValue = message.getHeaders().get(MessagingConstants.TOPIC_HEADER, String.class);
            var msg = publisher
                    .message(body.getBytes(StandardCharsets.UTF_8))
                    .annotation(MessagingConstants.FILTER_VALUE_NM, filterValue)
                    .contentType(contentType)
                    .property(filterProperty, filterValue);

            publisher.publish(msg, context -> {});

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
        return environment.connectionBuilder().host(host)
                .name(name)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    Management amqpManagement(Connection connection)
    {
        return connection.management();
    }

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
