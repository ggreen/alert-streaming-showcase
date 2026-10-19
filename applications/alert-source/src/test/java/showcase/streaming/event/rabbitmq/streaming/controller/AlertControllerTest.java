package showcase.streaming.event.rabbitmq.streaming.controller;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.integration.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.streaming.domains.Alert;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {


    private final Alert alert = JavaBeanGeneratorCreator.of(Alert.class).create();
    private AlertController subject;
    @Mock
    private Publisher<Alert> publisher;

    @BeforeEach
    void setUp() {
        subject = new AlertController(publisher);
    }

    @Test
    void given_alert_when_pub_then_publish() {
        subject.publish(alert);

        verify(publisher).send(any());
    }
}