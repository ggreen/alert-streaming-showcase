package io.cloudNativeData.alarm.ai.source.service;

import io.cloudNativeData.alarm.ai.source.domains.ProcessSummary;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.cloudNativeData.alarm.ai.source.service.ai.AlertsModelInference;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.List;


/**
 * This service detects alerts based on a given list of
 * activities.
 *
 * @author gregory green
 */

@Service
@Slf4j
public class InMemoryAlDetectorService implements AlertDetectorService{
    private final ListRepository<Activity> activityRepository;
    private final AlertsModelInference modelInference;
    private final int batchCount;
    private final long delayMs;
    private Long lastCheckTime = null;
    private final ProcessSummary summary = new ProcessSummary();

    public InMemoryAlDetectorService(ListRepository<Activity> activityRepository,
                                     AlertsModelInference modelInference,
                                     @Value("${alerts.inference.batch}")
                                     int batchCount,
                                     @Value("${alerts.inference.delayMs}")
                                     long delayMs) {
        this.activityRepository = activityRepository;
        this.modelInference = modelInference;
        this.batchCount = batchCount;
        this.delayMs = delayMs;
    }

    /**
     * Save to the repository and determine alerts
     * based on recent activities.
     *
     * @param activity the current activity
     * @return the list of alerts
     */
    @Override
    public List<Alert> detectAlerts(Activity activity) {
        log.info("Saving activity: {}",activity);

        activityRepository.save(activity);

        summary.setActivityCount(summary.getActivityCount()+1);

        log.info("Detecting alerts for activity: {}",activity);

        var detectedAlerts = checkForAlerts();

        if(detectedAlerts != null && !detectedAlerts.isEmpty()){
            summary.setAlertCount(summary.getAlertCount()+detectedAlerts.size());
        }

        log.info("summary: {}",summary);
        return detectedAlerts;
    }

    @Override
    public synchronized List<Alert> checkForAlerts() {
        long currentTime= System.currentTimeMillis();
        var activities = (List<Activity>) activityRepository.findAll();
        log.info("Checking if activities size:{} > {} (min batch size) or currentTime - {}: lastCheckTime > {}: delayMs",
                activities.size(),batchCount,lastCheckTime,delayMs);

        try{
            if(activities.size() > batchCount ||
                    (lastCheckTime!=null && (currentTime - lastCheckTime) > delayMs))
            {
                return getAlerts(activities);
            }
        }
        finally {
            lastCheckTime = System.currentTimeMillis();
        }

        return null;
    }

    @Override
    public ProcessSummary summarize() {
        return this.summary;
    }

    @Nullable
    private List<Alert> getAlerts(List<Activity> activities) {
        var alerts = this.modelInference.determineAlert(activities);

        if(alerts != null && !alerts.isEmpty())
        {
            log.info("Deleting activities from repository");
            activityRepository.deleteAll();
        }

        return alerts;
    }
}
