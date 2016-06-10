package ch.tie.perf.scenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class StatisticsScenario implements Scenario {

  private final Map<String, Map<Long, Long>> statistics;


  public StatisticsScenario() {
    statistics = new ConcurrentHashMap<String, Map<Long, Long>>();
  }

  protected void updateStatistics(long durationInNanos, String key) {

    Map<Long, Long> map = statistics.getOrDefault(key, new ConcurrentHashMap<Long, Long>());
    map.put(System.currentTimeMillis(), durationInNanos / 1000000);
    statistics.putIfAbsent(key, map);
  }

  @Override
  public Map<String, Map<Long, Long>> getStatistics() {
    return statistics;
  }
}
