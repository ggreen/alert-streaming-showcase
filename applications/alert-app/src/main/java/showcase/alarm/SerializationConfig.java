package showcase.alarm;

import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SerializationConfig {
    @Bean
    Converter<byte[], Alert> alertConverter(ObjectMapper objectMapper)
    {
        return msg -> objectMapper.readValue(new String(msg), Alert.class);
    }

    @Bean
    Converter<byte[], Activity> activityConverter(ObjectMapper objectMapper)
    {
        return msg -> objectMapper.readValue(new String(msg), Activity.class);
    }
}
