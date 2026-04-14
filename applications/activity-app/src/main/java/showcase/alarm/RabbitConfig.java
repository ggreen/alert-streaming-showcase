package showcase.alarm;

import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import showcase.streaming.domains.Activity;

@Configuration
@Slf4j
public class RabbitConfig {

    private int instanceCount = 2;
    private String superStreamName = "activities.super.stream";
    @Value("${spring.application.name:activity-app}")
    private String consumerName;

    @Bean("streamEnv")
    Environment env(){

        Environment environment = Environment.builder().build();

        //create stream
        environment.streamCreator().name(superStreamName)
                .superStream()
                .partitions(instanceCount).creator()
                .create();

        //offset tracking


        return environment;
    }


    @Bean
    Consumer consumer(@Qualifier("streamEnv") Environment environment,
                      Converter<byte[], Activity> converter,
                      java.util.function.Consumer<Activity> consumer){

        return environment.consumerBuilder()
                .superStream(superStreamName)
                .name(consumerName)
                .singleActiveConsumer()
//                .noTrackingStrategy()
                .messageHandler((context, message) -> {
                    consumer.accept(converter.convert(message.getBodyAsBinary()));
                })
                .build();
    }


}
