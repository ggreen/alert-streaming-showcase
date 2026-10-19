package showcase.alarm.ai.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;


@Configuration
@Slf4j
public class AiConfig {

    // Define a long timeout, e.g., 3 minutes (180 seconds)

    @Value("${ai.timeouts.seconds.connection}")
    private int connectionTimeoutSeconds;

    @Value("${ai.timeouts.seconds.read}")
    private int readTimeoutSeconds;

    @Bean
    public RestClient.Builder restClientBuilder() {
        // Create a request factory with custom timeouts
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMinutes(3)); // Wait up to 3 mins

        return RestClient.builder()
                .requestFactory(requestFactory);
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel){

        return ChatClient
                .builder(chatModel)
                .build();
    }
}
