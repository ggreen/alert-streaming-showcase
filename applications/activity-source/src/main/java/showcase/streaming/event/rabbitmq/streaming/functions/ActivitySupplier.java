package showcase.streaming.event.rabbitmq.streaming.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import showcase.streaming.domains.Activity;

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
public class ActivitySupplier implements Supplier<Message<Activity>> {
    private final Iterator<List<String>> csvLines;


    @Override
    public Message<Activity> get() {

        if (csvLines.hasNext()) {
            var line = csvLines.next();
            log.info("Events {}", line);

            var activityNote = line.get(4);
            var account = line.get(1);


            var activity = Activity.builder()
                    .id(line.get(0))
                    .account(line.get(1))
                    .icon(line.get(2))
                    .time(line.get(3))
                    .activity(activityNote)
                    .build();

            return MessageBuilder.withPayload(
                            activity)
                    .setHeader("account", activity.account())
                    .setHeader("level", activity.icon())
                    .build();
        }
        return null;
    }
}
