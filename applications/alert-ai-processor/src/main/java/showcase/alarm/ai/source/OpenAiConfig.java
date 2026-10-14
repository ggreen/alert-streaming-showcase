package showcase.alarm.ai.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.model.ollama.autoconfigure.OllamaChatAutoConfiguration;
import org.springframework.ai.model.ollama.autoconfigure.OllamaEmbeddingAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        prefix = "spring.ai.model",
        name = "chat",
        havingValue = "openai",
        matchIfMissing = false // set to true if you want this as default when the key isn't provided
)
@EnableAutoConfiguration(exclude = {
        OllamaEmbeddingAutoConfiguration.class,
        OllamaChatAutoConfiguration.class
})
@Slf4j
public class OpenAiConfig {

    public OpenAiConfig() {
        log.info("OpenAiConfig initialized");
    }
}