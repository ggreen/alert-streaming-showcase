package showcase.alarm.controller;

import lombok.RequiredArgsConstructor;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import showcase.streaming.domains.Alert;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("alert")
public class AlertController {

    @Value("${alert.refresh.rateSeconds:5}")
    private long refreshRateSecs = 5;
    private final ThreadFactory factory = Executors.defaultThreadFactory();
    private final ListRepository<Alert> repository;

    @GetMapping("alerts")
    public Flux<ServerSentEvent<Iterable<Alert>>> accounts() {
        var scheduler = Schedulers.newParallel(5,factory);
        return Flux.interval(Duration.ofSeconds(refreshRateSecs),scheduler)
                .map(sequence -> ServerSentEvent.<Iterable<Alert>> builder()
                        .data(repository.findAll())
                        .build());
    }

    /**
     * Save the alert
     * @param alert the alert to save
     */
    @PostMapping
    public void saveAlert(@RequestBody Alert alert) {
        repository.save(alert);
    }
}
