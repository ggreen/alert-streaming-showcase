package showcase.alarm;

import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.streaming.domains.Activity;

@Configuration
@Slf4j
public class RabbitConfig {

    private final int instanceCount = 2;
    private final String superStreamName = "activities.super.stream";

    @Value("${spring.application.name:activity-app}")
    private String consumerName;

    // Standard Spring Boot auto-configuration populated via VCAP_SERVICES when bound in Cloud Foundry
    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    // Stream port defaults to 5552 unless overridden by CF service binding credentials
    @Value("${spring.rabbitmq.stream.port:5552}")
    private int streamPort;

    @Bean("streamEnv")
    public Environment env() {
        log.info("Connecting RabbitMQ Stream Environment to CF bound host: {}:{}", host, streamPort);

        Environment environment = Environment.builder()
                .host(host)
                .port(streamPort)
                .username(username)
                .password(password)
                .virtualHost(virtualHost)
                .build();

        // Create super stream if it doesn't already exist
        environment.streamCreator()
                .name(superStreamName)
                .superStream()
                .partitions(instanceCount)
                .creator()
                .create();

        return environment;
    }

    @Bean
    public Consumer consumer(@Qualifier("streamEnv") Environment environment,
                             Converter<byte[], Activity> converter,
                             java.util.function.Consumer<Activity> consumer) {

        return environment.consumerBuilder()
                .superStream(superStreamName)
                .name(consumerName)
                .singleActiveConsumer()
                .messageHandler((context, message) -> {
                    consumer.accept(converter.convert(message.getBodyAsBinary()));
                })
                .build();
    }
}