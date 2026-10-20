package showcase.alarm.controller;

import com.jayway.jsonpath.spi.json.JakartaJsonProvider;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClearControllerTest {

    private ClearController subject;
    @Mock
    private ListRepository<Activity> activityRepo;
    @Mock
    private ListRepository<Alert> alertRepo;

    @BeforeEach
    void setUp() {
        subject = new ClearController(activityRepo,alertRepo);
    }

    @Test
    void given_repository_when_clear_then_clear_repos() {

        subject.clear();

        verify(activityRepo).deleteAll();
        verify(alertRepo).deleteAll();
    }
}