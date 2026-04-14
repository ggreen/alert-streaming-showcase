package showcase.alarm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import showcase.streaming.domains.Activity;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("activities")
public class ActivityController {

    @Value("${alert.refresh.rateSeconds:5}")
    private long refreshRateSecs;

    @Value("${alert.refresh.threadCount:5}")
    private int threadCount;
    private final ThreadFactory factory = Executors.defaultThreadFactory();
    private final ListRepository<Activity> repository;

    @GetMapping("activity")
    public Flux<ServerSentEvent<Iterable<Activity>>> accounts() {
        var scheduler = Schedulers.newParallel(threadCount,factory);
        return Flux.interval(Duration.ofSeconds(refreshRateSecs),scheduler)
                .map(sequence -> ServerSentEvent.<Iterable<Activity>> builder()
                        .data(repository.findAll())
                        .build());
    }

    @PostMapping("activity")
    public void saveActivity(@RequestBody Activity activity) {
        log.info("Saving: {}",activity);
           repository.save(activity);
    }
}
