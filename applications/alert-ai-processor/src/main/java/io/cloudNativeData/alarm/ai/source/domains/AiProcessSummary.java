package io.cloudNativeData.alarm.ai.source.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiProcessSummary {
    private long activityCount;
}
