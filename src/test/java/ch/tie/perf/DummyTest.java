package ch.tie.perf;

import org.junit.Test;

import java.time.LocalDateTime;

public class DummyTest {

    @Test
    public void test() {

        System.out.println(LocalDateTime.now().format(ch.tie.perf.statistic.StatisticsCollectorKt.getFilenamePrefixFormat()));
    }
}
