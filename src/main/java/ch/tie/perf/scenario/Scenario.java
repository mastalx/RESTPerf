package ch.tie.perf.scenario;

import java.util.Map;
import java.util.concurrent.Callable;

public interface Scenario extends Callable<Scenario> {

  public Map<String, Map<Long, Long>> getStatistics();
}
