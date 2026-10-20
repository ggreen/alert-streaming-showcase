package io.cloudNativeData.alarm.ai.source.service.ai;

import io.cloudNativeData.streaming.showcase.domains.Activity;
import io.cloudNativeData.streaming.showcase.domains.Alert;

import java.util.List;

@FunctionalInterface
public interface AlertsModelInference {
    List<Alert> determineAlert(List<Activity> activities);
}
