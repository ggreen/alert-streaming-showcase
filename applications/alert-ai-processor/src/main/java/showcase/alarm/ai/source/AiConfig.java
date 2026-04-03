package showcase.alarm.ai.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.client.ClientHttpRequestFactories;
//import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
//import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.restclient.RestClientCustomizer;
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

    /*
            return restClientBuilder -> restClientBuilder
                .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(connectionTimeoutSeconds))
                        .withReadTimeout(Duration.ofSeconds(readTimeoutSeconds))
                        )

                );
     */


//    @Bean
//    public RestClientCustomizer restClientCustomizer() {
//        log.info("TIMEOUTS ***  readTimeoutSeconds: {} connectionTimeoutSeconds: {}",readTimeoutSeconds, connectionTimeoutSeconds);
//        return restClientBuilder -> restClientBuilder
//                .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
//                        .withConnectTimeout(Duration.ofSeconds(connectionTimeoutSeconds))
//                        .withReadTimeout(Duration.ofSeconds(readTimeoutSeconds))
//                        )
//
//                );
//    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        // Create a request factory with custom timeouts
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMinutes(3)); // Wait up to 3 mins

        return RestClient.builder()
                .requestFactory(requestFactory);
    }



//    @Bean
//    ReactorNettyClientRequestFactory reactorNettyClientRequestFactory()
//    {
//        var factory = new ReactorNettyClientRequestFactory();
//        factory.setConnectTimeout(Duration.ofSeconds(connectionTimeoutSeconds));
//        factory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));
//
//        return factory;
//    }

//    @Bean
//    public WebClient.Builder webClientBuilder() {
//
//        HttpClient httpClient = HttpClient.create()
//                // 1. Connection Timeout (time to establish connection)
//                // Netty ChannelOption is in milliseconds
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutSeconds*60) // 10 seconds
//
//                // 2. Response Timeout (time to receive the full response)
//                .responseTimeout(Duration.ofSeconds(readTimeoutSeconds)) // 60 seconds
//
//                // 3. Optional: Fine-grained Read/Write timeouts (time between data chunks)
//                .doOnConnected(conn ->
//                        conn.addHandlerLast(
//                                new io.netty.handler.timeout.ReadTimeoutHandler(readTimeoutSeconds) // 30 seconds inactivity
//                        )
//                );
//
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient));
//    }

    @Bean
    ChatClient chatClient(ChatModel chatModel){

        return ChatClient
                .builder(chatModel)
                .defaultOptions(ChatOptions.builder()
                        .build())
                .build();
    }
}
