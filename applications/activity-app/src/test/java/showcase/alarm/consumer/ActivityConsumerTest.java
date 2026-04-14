package showcase.alarm.consumer;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.streaming.domains.Activity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivityConsumerTest {

    private ActivityConsumer subject;
    @Mock
    private ListRepository<Activity> repository;
    private final Activity activity = JavaBeanGeneratorCreator.of(Activity.class).create();

    @BeforeEach
    void setUp() {
        subject = new ActivityConsumer(repository);
    }

    @Test
    void accept() {
        subject.accept(activity);

        verify(repository).save(any());
    }
}