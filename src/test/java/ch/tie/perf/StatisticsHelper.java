package ch.tie.perf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.scenario.Scenario;

public class StatisticsHelper {

  private static final Logger LOGGER = LogManager.getLogger(StatisticsHelper.class);


  public ConcurrentHashMap<String, Map<Long, Long>> mergeStatistics(List<Future<? extends Scenario>> tasks) {
    ConcurrentHashMap<String, Map<Long, Long>> mergedStats = new ConcurrentHashMap<String, Map<Long, Long>>();

    LinkedList<Future<? extends Scenario>> queue = new LinkedList<>(tasks);

    while (!queue.isEmpty()) {
      Future<? extends Scenario> task = queue.pollFirst();
      try {
        Scenario scenario = task.get();
        Map<String, Map<Long, Long>> scenarioStats = scenario.getStatistics();
        mergeMaps(mergedStats, scenarioStats);
        List<Future<? extends Scenario>> spawnedTasks = scenario.getSpawnedTasks();
        queue.addAll(spawnedTasks);
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.error("error while getting task result", e);
      }
    }
    return mergedStats;
  }


  private void mergeMaps(Map<String, Map<Long, Long>> toStatistics, Map<String, Map<Long, Long>> fromStatistics) {
    for (Map.Entry<String, Map<Long, Long>> entry : fromStatistics.entrySet()) {
      String categoryName = entry.getKey();
      Map<Long, Long> category = toStatistics.get(categoryName);
      if (category == null) {
        category = new ConcurrentHashMap<Long, Long>();
        toStatistics.put(categoryName, category);
      }
      category.putAll(entry.getValue());
    }
  }

  public void printStatistics(String prefix, Map<String, Map<Long, Long>> statistics) throws IOException {

    for (Map.Entry<String, Map<Long, Long>> entry : statistics.entrySet()) {
      Map<Long, Long> stats = entry.getValue();
      Comparator<Long> longComparator = new Comparator<Long>() {

        @Override
        public int compare(Long o1, Long o2) {
          return Long.compare(o1, o2);
        }
      };


      TreeMap<Long, Long> sorted = new TreeMap<>(longComparator);
      sorted.putAll(stats);

      String categoryName = entry.getKey();
      LOGGER.info(categoryName + ": " + sorted);

      List<String> lines = new ArrayList<String>();
      for (Map.Entry<Long, Long> timeMeasurement : sorted.entrySet()) {
        lines.add(timeMeasurement.getKey() + ";" + timeMeasurement.getValue());
      }


      Files.write(Paths.get(prefix + categoryName), lines, StandardOpenOption.CREATE);
    }
  }

}
