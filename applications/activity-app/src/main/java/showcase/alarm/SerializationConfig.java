package showcase.alarm;

import nyla.solutions.core.patterns.conversion.Converter;
import org.jspecify.annotations.Nullable;
import org.springframework.cloud.function.context.config.JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SerializationConfig {

    @Bean
    MessageConverter messageConverter (Converter<byte[], Activity> converter){
        return new MessageConverter() {
            @Override
            public @Nullable Object fromMessage(Message<?> message, Class<?> targetClass) {
                var payload = message.getPayload();
                if (payload instanceof byte[] bytes) {
                    return converter.convert(bytes);
                }
                return payload;
            }

            @Override
            public @Nullable Message<?> toMessage(Object payload, @Nullable MessageHeaders headers) {
                if (payload instanceof byte[] bytes) {
                    payload = converter.convert(bytes);
                }
                var builder = MessageBuilder.withPayload(payload);

                if (headers != null) {}
                builder.setHeader(MessageHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON.toString()).build();

                return builder.build();
            }
        };
    }
    @Bean
    Converter<byte[], Activity> activityConverter(ObjectMapper objectMapper)
    {
        return msg -> objectMapper.readValue(new String(msg), Activity.class);
    }
}
