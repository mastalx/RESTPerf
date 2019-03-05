package ch.tie.perf;

import java.time.LocalDateTime;

import org.junit.Test;

import ch.tie.perf.scenario.Statistics;
import ch.tie.perf.statistic.StatisticsCollector;

public class DummyTest {

  @Test
  public void test() {
    System.out.println(LocalDateTime.now().format(new StatisticsCollector(new Statistics()).filenamePrefixFormat));
  }
}
