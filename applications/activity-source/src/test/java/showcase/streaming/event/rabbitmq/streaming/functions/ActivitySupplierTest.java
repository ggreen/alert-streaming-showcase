package showcase.streaming.event.rabbitmq.streaming.functions;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import showcase.streaming.domains.Activity;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ActivitySupplierTest
{
    private ActivitySupplier subject;

    private static final Activity activity = JavaBeanGeneratorCreator.of(Activity.class).create();
    private final List<List<String>> list = List.of(
            List.of(activity.id(),
                    activity.account(),
            activity.icon(),
                    activity.time(),
                    activity.activity()
    ));

    @BeforeEach
    void setUp() {
        subject = new ActivitySupplier(list.iterator());
    }

    @Test
    void given_line_when_get_then_return_activity() {

        var actual = subject.get();

        assertThat(actual.getPayload()).isEqualTo(activity);

    }
}