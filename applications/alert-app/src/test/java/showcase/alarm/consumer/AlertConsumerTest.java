package showcase.alarm.consumer;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.streaming.domains.Alert;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertConsumerTest {

    @Mock
    private ListRepository<Alert> repository;

    private AlertConsumer subject;
    private final Alert alert = JavaBeanGeneratorCreator.of(Alert.class).create();

    @BeforeEach
    void setUp() {
        subject = new AlertConsumer(repository);
    }

    @Test
    void accept() {

        subject.accept(alert);

        verify(repository).save(alert);
    }
}