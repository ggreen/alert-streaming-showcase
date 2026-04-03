package showcase.alarm.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.repository.memory.ListRepository;
import org.springframework.stereotype.Component;
import showcase.streaming.domains.Activity;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityConsumer implements Consumer<Activity> {
    private final ListRepository<Activity> repository;

    @Override
    public void accept(Activity activity) {

      log.info("Saving activity: {}",activity);
        repository.save(activity);
    }
}
