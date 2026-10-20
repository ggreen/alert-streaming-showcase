package io.cloudNativeData.showcase.streaming.source.generator.supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.cloudNativeData.showcase.streaming.source.generator.properties.AlertProperties;

import java.util.List;

import static nyla.solutions.core.util.Debugger.println;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertSupplierTest {

    private AlertSupplier subject;

    private final int idSequence = 0;
    private final String account = "junit";

    @Mock
    private AlertProperties alertProperties;

    private  AlertProperties alertPropertiesQa;

    @BeforeEach
    void setUp() {

        alertPropertiesQa = AlertProperties.builder()
                .account("account")
                .idStartSequence(1)
                .events(List.of("Event 1"))
                .levels(List.of("Level1"))
                .build();

    }


    @Test
    void notNull() {

        assertThrows(NullPointerException.class, () -> {
            subject = new AlertSupplier(alertProperties);
        });

    }

    @Test
    void notNull_levels() {

        when(alertProperties.getAccount()).thenReturn("account");
        when(alertProperties.getLevels()).thenReturn(List.of());
        when(alertProperties.getEvents()).thenReturn(List.of("e1"));
        assertThrows(NullPointerException.class, () -> {
            subject = new AlertSupplier(alertProperties);
        });

    }

    @Test
    void notNull_account() {

        when(alertProperties.getAccount()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> {
            subject = new AlertSupplier(alertProperties);
        });

    }

    @Test
    void get() {
        subject = new AlertSupplier(alertPropertiesQa);

        var actual = subject.get();
        println(actual);
        assertThat(actual).isNotNull();
        assertThat(actual.getHeaders().get("account")).isNotNull();
        assertThat(actual.getHeaders().get("level")).isNotNull();
    }

}