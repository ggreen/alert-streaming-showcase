package showcase.alarm.ai.source.functions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import showcase.alarm.ai.source.service.AlertDetectorService;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.List;
import java.util.function.Function;

/**
 * Alert AI processor
 * @author gregory green
 */

@Slf4j
@Component
public class AlertAiProcessor implements Function<Activity,List<Message<Alert>>> {

    private final AlertDetectorService detectorService;

    public AlertAiProcessor(AlertDetectorService detectorService) {
        this.detectorService = detectorService;
    }

    @Override
    public List<Message<Alert>> apply(Activity activity) {

        log.info("INPUT: {}", activity);
        var alerts = detectorService.detectAlerts(activity);

        if(alerts != null && !alerts.isEmpty())
            return alerts.stream().map( alert -> MessageBuilder.withPayload(alert)
                            .setHeader("account",
                                    activity.account())
                            .setHeader("level",
                                    alert.level())
                            .setHeader("contentType","application/json")
                            .build())

                    .toList();

        return null;
    }
}

