package io.cloudNativeData.alarm.ai.source.domains;

import lombok.Builder;
import io.cloudNativeData.streaming.showcase.domains.Activity;
import io.cloudNativeData.streaming.showcase.domains.Alert;

import java.util.List;

@Builder
public record AlertRecord(List<Activity> activities,List<Alert> alerts) {
}
