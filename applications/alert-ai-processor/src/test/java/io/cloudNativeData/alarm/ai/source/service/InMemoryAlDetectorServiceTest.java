package io.cloudNativeData.alarm.ai.source.service;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.cloudNativeData.alarm.ai.source.service.ai.AlertsModelInference;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {

        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,1,delayMs);
    }

    @Test
    void given_activity_when_increaseActivityCount() {

        var before = subject.summarize();

        subject.detectAlerts(activity);

        var after = subject.summarize();

        assertThat(before.getActivityCount()).isGreaterThanOrEqualTo(after.getActivityCount());
    }

    @Test
    void givenHashMoreThanMin_when_detectAlerts_Then_Call() {

        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,1,delayMs);

        List<Alert> expected = List.of(alert);
        var activities = List.of(activity,activity);

        when(activityRepository.findAll())
                .thenReturn(activities);
        when(alertsModelinference.determineAlert(activities)).thenReturn(expected);

        var actual = subject.detectAlerts(activity);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void givenHashLessThanMin_when_detectAlerts_Then_DoNotInfernece() {

        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,10,delayMs);

        List<Alert> expected = List.of(alert);
        var activities = List.of(activity,activity);

        when(activityRepository.findAll())
                .thenReturn(activities);
//        when(modelinference.determineActivities(activities)).thenReturn(expected);

        var actual = subject.detectAlerts(activity);

        assertThat(actual).isNull();
    }


    @Test
    void given_activities_with_alert_when_checkAlerts_then_Sends_Alerts() {

        int batchCount = 2;
        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,batchCount,delayMs);

        List<Activity> activities = List.of(activity,activity,activity);
        when(activityRepository.findAll()).thenReturn(activities);
        List<Alert> expected  = List.of(alert);
        when(alertsModelinference.determineAlert(any())).thenReturn(expected);

        List<Alert> alerts =  subject.checkForAlerts();

        assertThat(alerts).isNotEmpty();
        verify(activityRepository).deleteAll();

    }

    @Test
    void given_activities_with_alert_lessThanThreshold_but_AfterDelay_when_checkForAlerts_after_alerts() throws InterruptedException {

        int batchCount = 2;
        long delayMs = 10;
        subject = new InMemoryAlDetectorService(activityRepository, alertsModelinference,batchCount,delayMs);
        when(activityRepository.findAll())
                .thenReturn(List.of(activity))
                .thenReturn(List.of(activity,activity));

        List<Alert> expected  = List.of(alert);
        when(alertsModelinference.determineAlert(any())).thenReturn(expected);

        List<Alert> alerts =  subject.detectAlerts(activity);
        assertThat(alerts).isNull();

        //Sleep past delay
        Thread.sleep(delayMs+1);

        alerts =  subject.detectAlerts(activity);

        assertThat(alerts).isNotEmpty();
        verify(activityRepository).deleteAll();
    }
}