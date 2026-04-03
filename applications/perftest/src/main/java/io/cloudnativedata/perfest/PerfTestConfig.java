package io.cloudnativedata.perfest;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.util.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import showcase.streaming.domains.Activity;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class PerfTestConfig {
    /**
     * Loop 3 Million times
     */
    private static final Long loopCount = 3000000L;

    @Value("${bathSize:10000}")
    private int batchSize;

    @Value("${subBatchSize:5000}")
    private int subBatchSize;

    @Value("${perfTestStreamName:perfTest}")
    private String perfTestStreamName;

    @Value("${eventMessage:That's alot}")
    private String eventMessage;


    @Bean
    RabbitStreamTemplate template(Environment environment) {

        //Create Stream
        environment.streamCreator().stream(perfTestStreamName).create();

        var template = new RabbitStreamTemplate(environment, perfTestStreamName);
        template.setProducerCustomizer((stream, builder) -> {
            builder.batchSize(batchSize);
            builder.subEntrySize(subBatchSize);
        });

        return template;
    }


    @Bean
    Message msg(RabbitStreamTemplate template, JsonMapper mapper) {
        var event = Activity.builder().activity(eventMessage).build();
        var msgPayload = mapper.writeValueAsString(event);
        log.info("Sending event: {}", msgPayload);

        return template.messageBuilder().addData(msgPayload.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    @Bean
    ApplicationRunner applicationRunner(RabbitStreamTemplate template, Message streamMsg) {
        return args -> {

            var start = System.currentTimeMillis();

            for (int i = 0; i < loopCount; i++) {

                template.send(streamMsg);
            }

            var end = System.currentTimeMillis();
            var thruPut = (loopCount / (end - start)) * 1000;

            log.info("{} per second", Text.format().formatNumber(thruPut));
        };
    }
}
