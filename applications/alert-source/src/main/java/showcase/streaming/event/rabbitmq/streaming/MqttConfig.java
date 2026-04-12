package showcase.streaming.event.rabbitmq.streaming;

import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.util.Debugger;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageChannel;
import showcase.streaming.event.rabbitmq.streaming.constants.MessagingConstants;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * MqttConfig
 *
 * @author Gregory Green
 */
@Configuration
@Profile("mqtt")
@Slf4j
public class MqttConfig
{
    @Value("${spring.application.name:http-mqtt-source}")
    private String clientId;

    @Value("${mqtt.connectionUrl:tcp://localhost:1883}")
    private String connectionUrl;

    @Value("${mqtt.userName:guest}")
    private String userName;

    @Value("${mqtt.userPassword:guest}")
    private String userPassword;

    @Value("${mqtt.timeout:500}")
    private int timeout;

    IMqttClient mqttClient() throws MqttException
    {
        var mqttClient = new MqttClient(connectionUrl, clientId, new MemoryPersistence());

        // 2. Set Connection Options
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setCleanStart(true); // Setting clean start to true (clean session in MQTT v3)
        options.setConnectionTimeout(timeout);
        options.setAutomaticReconnect(true);

        // 3. Connect to the Broker3
        log.info("Attempting to connect to broker: {}", connectionUrl);
        mqttClient.connect(options);

        return mqttClient;
    }

    @Bean("publisher")
    MessageChannel publisher() throws MqttException {
        final IMqttClient client = mqttClient();

        return (message,timeout) ->
        {
            try {
                var payload = (String) message.getPayload();
                var httpHeaders = message.getHeaders();
                var msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
                var properties = new MqttProperties();
                var userProperties = new ArrayList<UserProperty>();

                for (var entry : httpHeaders.entrySet()) {
                    var value = entry.getValue();
                    userProperties.add(new UserProperty(entry.getKey(), value.toString()));
                }

                String topic = httpHeaders.get(MessagingConstants.TOPIC_HEADER, String.class);

                properties.setUserProperties(userProperties);
                msg.setProperties(properties);
                msg.setQos(1);
                client.publish(topic, msg);

                log.info(" Published to topic: {} body: {}", topic, payload);
                return true;
            } catch (MqttException e) {
                log.error(Debugger.stackTrace(e));
                throw new RuntimeException(e);
            }
        };
    }
}
