package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class StatisticsScenario implements Scenario {

  private static final Logger LOGGER = LogManager.getLogger(StatisticsScenario.class);
  private final Map<String, List<Pair<Long, Long>>> statistics;

  private final List<Future<Scenario>> taskList = new ArrayList<>();

  public StatisticsScenario() {
    statistics = new ConcurrentHashMap<String, List<Pair<Long, Long>>>();
  }

  protected void updateStatistics(long durationInNanos, String key) {

    List<Pair<Long, Long>> map = statistics.getOrDefault(key, new ArrayList<Pair<Long, Long>>());
    Pair<Long, Long> entry = new Pair<>(System.nanoTime(), durationInNanos / 1000000);
    LOGGER.trace("updating Statistics:" + key + ", value: " + entry);
    map.add(entry);

    statistics.putIfAbsent(key, map);
  }

  public void addChildTask(Future<Scenario> childTask) {
    taskList.add(childTask);
  }

  @Override
  public Map<String, List<Pair<Long, Long>>> getStatistics() {
    return statistics;
  }

  @Override
  public List<Future<Scenario>> getSpawnedTasks() {
    return taskList;
  }
}
