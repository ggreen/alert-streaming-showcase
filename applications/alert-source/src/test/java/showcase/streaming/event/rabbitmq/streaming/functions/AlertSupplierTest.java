package showcase.streaming.event.rabbitmq.streaming.functions;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import showcase.streaming.domains.Alert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlertSupplierTest {

    private AlertSupplier subject;
    private final static Alert expected = JavaBeanGeneratorCreator.of(Alert.class).create();

    private final List<List<String>> expectedList = List.of(
            List.of(expected.id()
                    ,expected.account()
                    ,expected.level(),
                    expected.time(),
                    expected.event())
    );

    @BeforeEach
    void setUp() {
        subject = new AlertSupplier(expectedList.iterator());
    }

    @Test
    void given_line_when_get_return_expected() {

        var actual = subject.get();
        assertThat(expected.id()).isEqualTo(actual.getPayload().id());
        assertThat(expected.time()).isEqualTo(actual.getPayload().time());
        assertThat(expected.account()).isEqualTo(actual.getPayload().account());
        assertThat(actual.getPayload().event()).contains(expected.event());
    }
}