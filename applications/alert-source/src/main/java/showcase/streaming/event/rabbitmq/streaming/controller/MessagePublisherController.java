package showcase.streaming.event.rabbitmq.streaming.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import showcase.streaming.event.rabbitmq.streaming.constants.MessagingConstants;

import java.util.Map;

@RestController
@Slf4j
public class MessagePublisherController {


    private final MessageChannel publisher;

    public MessagePublisherController(@Qualifier("publisher") MessageChannel publisher) {
        this.publisher = publisher;
    }

    @PostMapping("publisher")
    @ResponseStatus(HttpStatus.OK)
    void post(@RequestHeader Map<String, String> httpHeaders, @RequestParam String topic, @RequestBody String body) throws MqttException {
        publisher.send(MessageBuilder.withPayload(body)
                        .copyHeaders(httpHeaders)
                        .setHeader(MessagingConstants.TOPIC_HEADER,topic)
                        .build()
                );
    }

}
