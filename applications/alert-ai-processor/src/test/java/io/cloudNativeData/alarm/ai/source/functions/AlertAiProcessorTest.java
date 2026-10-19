package io.cloudNativeData.alarm.ai.source.functions;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.cloudNativeData.alarm.ai.source.service.AlertDetectorService;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertAiProcessorTest {

    private AlertAiProcessor subject;
    private final Activity activity = JavaBeanGeneratorCreator.of(Activity.class).create();
    @Mock
    private AlertDetectorService detectorService;
    private final Alert alert = JavaBeanGeneratorCreator.of(Alert.class).create();

    @BeforeEach
    void setUp() {
        subject = new AlertAiProcessor(detectorService);
    }

    @Test
    void givenActivity_with_small_activity_sample_size_when_apply_then_check_for_alerts() {

        List<Alert> results = List.of(alert);
        when(detectorService.detectAlerts(activity)).thenReturn(results);
        var actual = subject.apply(activity);

        assertThat(actual).isNotEmpty();

    }
}