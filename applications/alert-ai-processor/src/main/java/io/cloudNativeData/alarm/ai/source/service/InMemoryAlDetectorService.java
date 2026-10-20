package io.cloudNativeData.alarm.ai.source.service;

import io.cloudNativeData.alarm.ai.source.domains.AlertRecord;
import io.cloudNativeData.alarm.ai.source.domains.ProcessSummary;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.data.collections.CapacityList;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.cloudNativeData.alarm.ai.source.service.ai.AlertsModelInference;
import io.cloudNativeData.streaming.showcase.domains.Activity;
import io.cloudNativeData.streaming.showcase.domains.Alert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


/**
 * This service detects alerts based on a given list of
 * activities.
 *
 * @author gregory green
 */

@Service
@Slf4j
public class InMemoryAlDetectorService implements AlertDetectorService {
    private final ListRepository<Activity> activityRepository;
    private final AlertsModelInference modelInference;
    private final int batchCount;
    private final ProcessSummary summary;
    private final AtomicLong activityCount = new AtomicLong(0);
    private final AtomicLong alertCount = new AtomicLong(0);
    private final AtomicLong lastCheckTime = new AtomicLong(System.currentTimeMillis());

    public InMemoryAlDetectorService(ListRepository<Activity> activityRepository,
                                     AlertsModelInference modelInference,
                                     @Value("${alerts.inference.batch}") int batchCount,
                                     @Value("${alerts.inference.delayMs}") long delayMs,
                                     @Value("${alerts.max.records:20}") int maxAlertsRecordsCount) {
        this.activityRepository = activityRepository;
        this.modelInference = modelInference;
        this.batchCount = batchCount;
        this.summary = ProcessSummary.builder()
                .activityCount(0L)
                .alertCount(0L)
                .alertRecords(new CapacityList<>(maxAlertsRecordsCount))
                .build();
    }

    @Override
    public synchronized List<Alert> detectAlerts(Activity activity) {
        log.info("Saving activity: {}", activity);
        activityRepository.save(activity);
        summary.setActivityCount(activityCount.incrementAndGet());

        log.info("Detecting alerts for activity: {}", activity);
        return checkForAlerts();
    }

    public synchronized List<Alert> checkForAlerts() {
        var activities = new ArrayList<>((Collection<Activity>) activityRepository.findAll());

        if (activities.isEmpty()) {
            return null;
        }

        boolean batchThresholdMet = activities.size() >= batchCount;

        if (batchThresholdMet ) {
            return processAlerts(activities);
        }

        return null;
    }

    private List<Alert> processAlerts(List<Activity> activities) {
        var alerts = this.modelInference.determineAlert(activities);

        if (alerts != null && !alerts.isEmpty()) {
            activityRepository.deleteAll();

            summary.getAlertRecords().add(AlertRecord.builder()
                    .activities(activities) // Defensive copy preserved
                    .alerts(alerts)
                    .build());

            summary.setAlertCount(alertCount.addAndGet(alerts.size()));
        }

        return alerts;
    }

    @Override
    public ProcessSummary summarize() {
        return this.summary;
    }
}