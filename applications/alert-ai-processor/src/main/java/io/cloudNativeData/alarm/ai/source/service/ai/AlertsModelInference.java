package io.cloudNativeData.alarm.ai.source.service.ai;

import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.List;

@FunctionalInterface
public interface AlertsModelInference {
    List<Alert> determineAlert(List<Activity> activities);
}
