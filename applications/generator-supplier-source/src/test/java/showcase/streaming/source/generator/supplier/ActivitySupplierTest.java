package showcase.streaming.source.generator.supplier;

import nyla.solutions.core.io.csv.CsvReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static nyla.solutions.core.util.Debugger.println;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivitySupplierTest {

    private ActivitySupplier subject;
    private final static int idSequenceStart = 0;

    @Mock
    private CsvReader csvReader;



    @Test
    void errorWhenCsvReaderIsEmpty() {
        when(csvReader.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> new ActivitySupplier(idSequenceStart,csvReader));
    }

    @Test
    void get() {
        when(csvReader.size()).thenReturn(1);
        List<String> row = asList("junit","icon","Running activity");
        when(csvReader.row(anyInt())).thenReturn(row);

        subject = new ActivitySupplier(idSequenceStart,csvReader);

        var actual = subject.get();

        println(actual);
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isNotNull();
        assertThat(actual.time()).isNotNull();
        assertThat(actual.activity()).isNotNull();
        assertThat(actual.icon()).isNotNull();

    }
}