package io.cloudNativeData.alarm.ai.source.controller;

import io.cloudNativeData.alarm.ai.source.domains.ProcessSummary;
import io.cloudNativeData.alarm.ai.source.service.AlertDetectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertAiControllerTest {

    private AlertAiController subject;

    @Mock
    private AlertDetectorService service;
    @Mock
    private ProcessSummary expected;

    @BeforeEach
    void setUp() {
        subject = new AlertAiController(service);
    }

    @Test
    void getSummary() {
        when(service.summarize()).thenReturn(expected);

        var actual  = subject.summarize();

        assertThat(actual).isEqualTo(expected);
    }
}