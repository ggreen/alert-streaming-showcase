package showcase.alarm.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.stereotype.Component;
import showcase.streaming.domains.Alert;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlertConsumer implements Consumer<Alert> {

    private final ListRepository<Alert> repository;

    @Override
    public void accept(Alert alert) {
        log.info("processing alert: {}",alert);
        repository.save(alert);
    }
}
