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

  public void mergeStatistics(Map<String, Map<Long, Long>> otherStatistics) {
    for (Map.Entry<String, Map<Long, Long>> entry : otherStatistics.entrySet()) {
      String categoryName = entry.getKey();
      Map<Long, Long> category = statistics.get(categoryName);
      if (category == null) {
        category = new ConcurrentHashMap<Long, Long>();
        statistics.put(categoryName, category);
      }
      category.putAll(entry.getValue());
    }

  }

}
