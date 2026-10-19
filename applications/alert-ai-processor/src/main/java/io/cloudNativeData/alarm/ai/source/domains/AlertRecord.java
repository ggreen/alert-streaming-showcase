package io.cloudNativeData.alarm.ai.source.domains;

import lombok.Builder;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.List;

@Builder
public record AlertRecord(List<Activity> activities,List<Alert> alerts) {
}
