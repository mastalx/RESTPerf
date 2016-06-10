package ch.tie.perf.scenario;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Scenario extends Callable<Scenario> {

  public Map<String, Map<Long, Long>> getStatistics();

  public List<Future<? extends Scenario>> getSpawnedTasks();
}
