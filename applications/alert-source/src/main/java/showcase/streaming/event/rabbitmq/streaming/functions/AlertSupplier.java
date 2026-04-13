package showcase.streaming.event.rabbitmq.streaming.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import showcase.streaming.domains.Alert;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author gregory green
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertSupplier implements Supplier<Message<Alert>> {
    private final Iterator<List<String>> csvLines;
    @Value("${accountEventPrefix:For user }")
    private String accountEventPrefix="For user ";

    @Value("${accountEventSuffix: }")
    private String accountEventSuffix = " ";


    @Override
    public Message<Alert> get() {

        if (csvLines.hasNext()) {
            var line = csvLines.next();
            log.info("Events {}", line);

            var event = line.get(4);
            var account = line.get(1);

            if (event != null && !event.contains(account)) {
                event = accountEventPrefix + account + accountEventSuffix + event;
            }


            var alert = Alert.builder()
                    .id(line.get(0))
                    .account(line.get(1))
                    .level(line.get(2))
                    .time(line.get(3))
                    .event(event)
                    .build();

            return MessageBuilder.withPayload(
                            alert)
                    .setHeader("account", alert.account())
                    .setHeader("level", alert.level())
                    .build();
        }
        return null;
    }
}
