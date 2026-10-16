package showcase.streaming.source.generator.supplier;

import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.io.csv.CsvReader;
import nyla.solutions.core.util.Digits;
import nyla.solutions.core.util.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import showcase.streaming.domains.Activity;

import java.util.Date;
import java.util.function.Supplier;

@Component
@Slf4j
public class ActivitySupplier implements Supplier<Activity> {
    private int idSequence;

    private final CsvReader csvReader;

    public ActivitySupplier(@Value("${generator.activity.id.start:0}") int idSequence,
                            CsvReader csvReader) {
        if(csvReader.isEmpty())
            throw new IllegalArgumentException("Csv Reader size must be > 0");

        this.idSequence = idSequence;
        this.csvReader = csvReader;
    }

    @Override
    public Activity get() {


        if(idSequence >= csvReader.size())
            idSequence = 0;

        log.info("idSequence: {}",idSequence);

        var row = csvReader.row(idSequence++);

        log.info("row: {}",row);

        var  activity = Activity.builder()
                .time(Text.format().formatDate("hh:mm a ss S ", new Date()))
                .account(row.get(0))
                .icon(row.get(1))
                .activity(row.get(2))
                .build();
        log.info("activity: {}",activity);
        return activity;
    }

    private String id() {
        return String.valueOf(idSequence++);
    }
}
