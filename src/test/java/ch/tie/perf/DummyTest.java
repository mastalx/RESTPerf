package ch.tie.perf;

import ch.tie.perf.statistic.StatisticsCollectorKt;
import org.junit.Test;

import java.time.LocalDateTime;

public class DummyTest {

    @Test
    public void test() {

        System.out.println(LocalDateTime.now().format(StatisticsCollectorKt.filenamePrefixFormat));
    }
}
