package io.cloudNativeData.alarm.ai.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import io.cloudNativeData.alarm.ai.source.service.ai.AlertsModelInference;
import io.cloudNativeData.streaming.showcase.domains.AlertList;

@Configuration
@Profile("iot")
@Slf4j
public class ServiceIotHomeSecurityConfig {
    private static final String prompt = """
            Given the following activities, identify alerts,
            For each alert response with the "level" with values of (critical, high, medium, low),
            the time and the event which contains why you believe this is an alert
            
            ONLY RESPONSE WITH Json Object fields level, account, time, and event
            
            ADD details on WHY you identified the alert and the level in the field "event" 
            
            ```json
            {listOfActivities}
            ```
            
            Use Context Below
            CONTEXT:
            DO NOT alert when Opened door or Garage Door AND the door is closed at a later time
            DO NOT alert when Alarm System Turned On
            DO NOT alert when Alarm System Turned OFF Successfully
            DO NOT alert then Thermostat Set least Than 72°F (Heat)
            Front Door Opened with No Front Door Closed is a high Alert
            Garage Door Opened with No Garage Door Closed is a medium Alert
            Alarm System TRIGGER possible BREAK-IN is a critical Alert
            Window Broken ALARM TRIGGERED is a critical Alert
            Front Door Broken ALARM TRIGGERED is a critical Alert
            Camera Broken ALARM TRIGGERED  is a critical Alert
            """;
    @Bean
    AlertsModelInference alertsModelInference(ChatClient chatClient){

        return listOfActivities -> {

            synchronized (this) {

                var alertList = chatClient.prompt()
                        .user(u -> u.text(prompt)
                                .param("listOfActivities", listOfActivities))
                        .call()
                        .entity(AlertList.class);
                log.info("alerts: {}", alertList);

                return alertList != null ? alertList.alerts() : null;
            }
        };
    }
}
