package io.cloudNativeData.alarm.ai.source.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessSummary {
    private long activityCount;
    private long alertCount;
    private List<AlertRecord> alertRecords;
}
