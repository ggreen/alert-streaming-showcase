package io.cloudNativeData.alarm.ai.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import io.cloudNativeData.alarm.ai.source.service.ai.AlertsModelInference;
import showcase.streaming.domains.AlertList;

@Configuration
@Profile("finance")
@Slf4j
public class ServiceFinancePaymentsConfig {
    private static final String prompt = """
            
            Given the following payment activities, identify potential fraud alerts,
            For each alert response with the "level" with values of (critical, high, medium, low),
            the time and the event which contains why you believe this is an alert
            ONLY RESPONSE the Json Object fields level, time, and event
            ADD details on WHY you identified the alert and the level in the field "event"
            
            [ACTIVITIES]
            ```json
            {listOfActivities}
            ```
            Use Context Below
            CONTEXT:
            
            Activities for the same account from one more than 1 terminal_id within 1 minute time differences is a critical alert.
            Activities for the same account from one more than 1 terminal_id within 5 minute time differences is a high alert.
            Activities with a series of very small transactions (e.g., $0.01 or $1.00) in rapid succession is a high alert
            Activities with merchant_id: MERCH_5555 and amount less than 10 is LOW alert, if amount greater than 100 that create high alert
            Activities for the same account from one more than 1 terminal_id greater than a 1-minute time difference is not an alert
            """;
    @Bean
    AlertsModelInference alertsModelInference(ChatClient chatClient){

        return listOfActivities -> {

            synchronized (this) {

                log.info("Calling model with prompt: {} and listOfActivities: {} \n\n WAITING FOR RESPONSE ....",
                        prompt,listOfActivities);

                var alertList = chatClient.prompt()
                        .user(u -> u.text(prompt)
                                .param("listOfActivities", listOfActivities))
                        .call()
                        .entity(AlertList.class);
                log.info("Model results of alerts: {}", alertList);

                return alertList != null ? alertList.alerts() : null;
            }
        };
    }
}
