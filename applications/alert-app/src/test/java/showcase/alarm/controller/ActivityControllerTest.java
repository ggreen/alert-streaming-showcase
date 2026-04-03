package showcase.alarm.controller;

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
class ActivityControllerTest {

    private ActivityController subject;
    private final Activity activity = JavaBeanGeneratorCreator.of(Activity.class).create();
    @Mock
    private ListRepository<Activity> repository;

    @BeforeEach
    void setUp() {
        subject = new ActivityController(repository);
    }

    @Test
    void saveActivity() {

        subject.saveActivity(activity);

        verify(repository).save(any());
    }
}