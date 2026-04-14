package showcase.alarm;

import com.rabbitmq.stream.OffsetSpecification;
import nyla.solutions.core.util.Text;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RabbitConfig {

    @Bean
    ListenerContainerCustomizer<MessageListenerContainer> customizer() {
        return (msgListenerContainer, dest, group) -> {
            if (msgListenerContainer instanceof StreamListenerContainer streamContainer) {
                streamContainer.setConsumerCustomizer((name, builder) -> {

                    builder.subscriptionListener(
                            subscriptionContext -> subscriptionContext
                                    .offsetSpecification(OffsetSpecification.first()));

                });
            }
        };
    }
}
