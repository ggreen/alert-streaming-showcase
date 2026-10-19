package io.cloudNativeData.alarm.ai.source.service;

import io.cloudNativeData.alarm.ai.source.domains.ProcessSummary;
import showcase.streaming.domains.Activity;
import showcase.streaming.domains.Alert;

import java.util.List;

/**
 * The alert detector service
 * @author gregory green
 */
public interface AlertDetectorService {
    /**
     *
     * @param activity the current activity
     * @return the list of detected alerts
     */
    List<Alert> detectAlerts(Activity activity);

    List<Alert> checkForAlerts();

    ProcessSummary summarize();
}
