package showcase.alarm.controller;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
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

    private AlertController subject;
    private final Alert alert = JavaBeanGeneratorCreator.of(Alert.class).create();

    @Mock
    private ListRepository<Alert> repository;

    @BeforeEach
    void setUp() {
        subject = new AlertController(repository);
    }

    @Test
    void accounts() {
        subject.saveAlert(alert);

        verify(repository).save(any());
    }
}