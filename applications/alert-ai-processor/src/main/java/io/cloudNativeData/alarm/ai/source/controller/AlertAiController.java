package io.cloudNativeData.alarm.ai.source.controller;

import io.cloudNativeData.alarm.ai.source.domains.AiProcessSummary;
import io.cloudNativeData.alarm.ai.source.service.AlertDetectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("alerts/ai")
@RequiredArgsConstructor
public class AlertAiController {

    private final AlertDetectorService alertDetectorService;
    public AiProcessSummary summarize() {
        return alertDetectorService.summarize();
    }
}
