package showcase.alarm.ai.source;

import com.rabbitmq.stream.OffsetSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.function.context.config.JsonMessageConverter;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.cloud.stream.config.ProducerMessageHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.outbound.RabbitStreamMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.StreamMessageProperties;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@Slf4j
public class RabbitConfig {


    @Value("${stream.activity.filter.value}")
    private String filterValue;

    @Value("${stream.activity.filter.name:account}")
    private String filterPropName;

    @Bean
    ListenerContainerCustomizer<MessageListenerContainer> customizer() {
        return (cont, dest, group) -> {
            if(cont instanceof StreamListenerContainer container)
            {
                //set filter
                container.setConsumerCustomizer((name, builder) -> {

                    log.info("Filtering consumer with value: {}",filterValue);
                    builder.noTrackingStrategy()
                            .filter().values(filterValue)
                                    .postFilter( message ->
                                            {
                                                return filterValue.equals(message
                                                        .getApplicationProperties()
                                                        .get(filterPropName));
                                            }

                                            );

                    builder.offset(OffsetSpecification.first());

                });
            }
        };
    }

    @Bean
    org.springframework.messaging.converter.MessageConverter messageConverter(JsonMapper objectMapper)
    {
      var jsonConvert =  new JsonMessageConverter(new JacksonMapper(objectMapper));

      return new MessageConverter() {
          @SneakyThrows
          @Override
          public Object fromMessage(Message<?> message, Class<?> targetClass) {
              log.info("fromMessage.payload: {}",message.getPayload());
              var results = jsonConvert.fromMessage(message,targetClass);
              log.info("results={}",results);
              if(results !=null)
                return results;

              if( message.getPayload() instanceof byte[] payloadBytes)
              {
                      var messageString = new String(payloadBytes);
                      log.info("messageString: {}, targetClass:{}",messageString,targetClass.getName());
                      return objectMapper.readValue(messageString,targetClass);
              }

              return null;
          }

          @Override
          public Message<?> toMessage(Object payload, MessageHeaders headers) {
              log.info("toMessage: {}",payload);
              return jsonConvert.toMessage(payload,headers);
          }
      };
    }

    @Bean
    ProducerMessageHandlerCustomizer<MessageHandler> handlerCustomizer() {
        return (hand, dest) -> {
            if(hand instanceof RabbitStreamMessageHandler handler)
            {
                var rabbitStreamTemplate = ((RabbitStreamTemplate) handler.getStreamOperations());
                handler.setConfirmTimeout(5000);

                rabbitStreamTemplate.setStreamConverter(new StreamMessageConverter() {
                    @Override
                    public org.springframework.amqp.core.Message toMessage(Object object, StreamMessageProperties messageProperties) throws MessageConversionException {
                        return null;
                    }

                    @Override
                    public com.rabbitmq.stream.Message fromMessage(org.springframework.amqp.core.Message message) throws MessageConversionException {

                        var streamBuilder = rabbitStreamTemplate.messageBuilder();
                        var applicationProperties = streamBuilder.applicationProperties();

                        //copy headers
                        for(var headerEntry : message.getMessageProperties()
                                    .getHeaders().entrySet())
                            applicationProperties.entry(headerEntry.getKey(),String.valueOf(headerEntry.getValue()));

                        return applicationProperties.messageBuilder()
                                .addData(message.getBody()).build();
                    }
                });
            }
        };
    }

}
