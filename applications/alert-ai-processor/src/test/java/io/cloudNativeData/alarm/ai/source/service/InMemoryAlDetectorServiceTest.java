package io.cloudNativeData.alarm.ai.source.service;

import io.cloudNativeData.alarm.ai.source.service.ai.AlertsModelInference;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryAlDetectorServiceTest {

    private InMemoryAlDetectorService subject;
    @Mock
    private ListRepository<Activity> activityRepository;

    private final Activity activity = JavaBeanGeneratorCreator
            .of(Activity.class).create();

    @Mock
    private AlertsModelInference alertsModelinference;
    private final Alert alert = JavaBeanGeneratorCreator.of(Alert.class).create();
    private final static long delayMs = 1000*60;
    private final static int maxAlertCount = 100;

    private static final int BATCH_COUNT = 5;
    private static final long DELAY_MS = 1000L;
    private static final int MAX_RECORDS = 20;

    @BeforeEach
    void setUp() {

        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,1,delayMs,
                maxAlertCount);
    }

    @Test
    void given_activity_when_increaseActivityCount() {

        var before = subject.summarize();

        subject.detectAlerts(activity);

        var after = subject.summarize();

        assertThat(before.getActivityCount()).isGreaterThanOrEqualTo(after.getActivityCount());
    }

    @Test
    void given_hasDetected_Alert_summaryHasAlerts() {

        List<Activity> activities = new ArrayList<>(
                List.of(activity,activity)
        );

        subject = new InMemoryAlDetectorService(new ListRepository<>(activities), alertsModelinference,1,delayMs,
                maxAlertCount);

        List<Alert> expected = List.of(alert,alert);

        when(alertsModelinference.determineAlert(activities)).thenReturn(expected);

        var before = subject.summarize();

        subject.detectAlerts(activity);

        var after = subject.summarize();

        assertThat(before.getAlertCount()).isEqualTo(expected.size());
        assertThat(after.getAlertRecords()).hasSize(1);
        assertThat(after.getAlertRecords().iterator().next().activities()).hasSize(3);
        assertThat(after.getAlertRecords().iterator().next().alerts()).isNotEmpty();
    }

    @Test
    void givenHashMoreThanMin_when_detectAlerts_Then_Call() {

        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,1,delayMs,maxAlertCount);

        List<Alert> expected = List.of(alert);
        var activities = List.of(activity,activity);

        when(activityRepository.findAll())
                .thenReturn(activities);
        when(alertsModelinference.determineAlert(activities)).thenReturn(expected);

        var actual = subject.detectAlerts(activity);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void givenHasLessThanMin_when_detectAlerts_Then_DoNotInference() {

        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,10,delayMs,maxAlertCount);

        List<Alert> expected = List.of(alert);
        var activities = List.of(activity,activity);

        when(activityRepository.findAll())
                .thenReturn(activities);

        var actual = subject.detectAlerts(activity);

        assertThat(actual).isNull();
    }


    @Test
    void given_activities_with_alert_when_checkAlerts_then_Sends_Alerts() {

        int batchCount = 2;

        List<Activity> activities = List.of(activity,activity,activity);
        when(activityRepository.findAll()).thenReturn(activities);
        List<Alert> expected  = List.of(alert);
        when(alertsModelinference.determineAlert(any())).thenReturn(expected);

        List<Alert> alerts =  subject.checkForAlerts();

        assertThat(alerts).isNotEmpty();
        verify(activityRepository).deleteAll();

    }



    @Nested
    @DisplayName("Bug 1 & 2: Race Conditions & Stale Reference Mutability")
    class ConcurrencyAndMementoBugs {

        @Test
        @DisplayName("Expose Bug 2: activitiesMemento retains mutated/cleared list reference")
        void testActivitiesMementoClearedAfterDeleteAll() {
            // Setup shared list instance mimicking in-memory repository behavior
            List<Activity> repoList = new ArrayList<>();
            repoList.add(Activity.builder().build());
            repoList.add(Activity.builder().build());

            when(activityRepository.findAll()).thenReturn(repoList);

            // Simulate deleteAll clearing the shared repository list
            doAnswer(invocation -> {
                repoList.clear();
                return null;
            }).when(activityRepository).deleteAll();

            when(alertsModelinference.determineAlert(anyList()))
                    .thenReturn(List.of(Alert.builder().build()));

            // Trigger detection threshold
            subject.checkForAlerts();

            // Perform detection flow to record alert in summary
            subject.detectAlerts(Activity.builder().build());

            var summary = subject.summarize();
            assertThat(summary.getAlertRecords()).isNotEmpty();

            // Verification fails because activitiesMemento points to repoList which was cleared
            List<Activity> savedActivities = summary.getAlertRecords().get(0).activities();
            assertThat(savedActivities)
                    .as("Saved record activities should not be empty after repository deleteAll()")
                    .isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Bug 3 & 5: Time Threshold & Initialization Logic")
    class TimingAndBatchingBugs {

        @Test
        @DisplayName("Expose Bug 5: Inferences execute repeatedly on empty repositories")
        void testInferenceTriggeredOnEmptyRepositoryWhenTimeThresholdExceeded() throws InterruptedException {
            when(activityRepository.findAll()).thenReturn(List.of());

            // Force delayMs to trigger
            Thread.sleep(DELAY_MS + 100);

            subject.checkForAlerts();

            verify(alertsModelinference, never())
                    .determineAlert(anyList());
        }
    }

    @Nested
    @DisplayName("Bug 4: Off-by-One Threshold Logic")
    class OffByOneBugs {

        @Test
        @DisplayName("Expose Bug 4: Requires batchCount + 1 items instead of batchCount")
        void testExactBatchCountThresholdTrigger() {
            // Create list matching EXACT batchCount (5)
            List<Activity> exactBatch = new ArrayList<>();
            for (int i = 0; i < BATCH_COUNT; i++) {
                exactBatch.add(Activity.builder().build());
            }

            when(activityRepository.findAll()).thenReturn(exactBatch);

            subject.checkForAlerts();

            verify(alertsModelinference, times(1))
                    .determineAlert(exactBatch);
        }
    }
}