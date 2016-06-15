package ch.tie.perf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.scenario.Pair;
import ch.tie.perf.scenario.Scenario;

public class StatisticsCollector {

  private static final Logger LOGGER = LogManager.getLogger(StatisticsCollector.class);
  private final SimpleDateFormat filenamePrefixFormat = new SimpleDateFormat("yyyyMMddHHmmss");


  public void collectAndPrintStatistics(List<Future<Scenario>> tasks, String experiment) {
    Map<String, List<Pair<Long, Long>>> mergedStats = new ConcurrentHashMap<String, List<Pair<Long, Long>>>();

    Date now = new Date();
    String fileNamePrefix = filenamePrefixFormat.format(now) + "_" + experiment + "_";

    LinkedList<Future<Scenario>> queue = new LinkedList<>(tasks);

    while (!queue.isEmpty()) {
      Future<Scenario> task = queue.pollFirst();
      try {
        Scenario scenario = task.get();
        Map<String, List<Pair<Long, Long>>> scenarioStats = scenario.getStatistics();
        mergeMaps(mergedStats, scenarioStats);
        List<Future<Scenario>> spawnedTasks = scenario.getSpawnedTasks();
        queue.addAll(spawnedTasks);
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.error("error while getting task result", e);
      }
    }
    printStatistics(fileNamePrefix, mergedStats);
  }


  private void mergeMaps(Map<String, List<Pair<Long, Long>>> toStatistics,
      Map<String, List<Pair<Long, Long>>> fromStatistics) {
    for (Map.Entry<String, List<Pair<Long, Long>>> category : fromStatistics.entrySet()) {
      String categoryName = category.getKey();
      List<Pair<Long, Long>> measurements = toStatistics.get(categoryName);
      if (measurements == null) {
        measurements = new ArrayList<Pair<Long, Long>>();
        toStatistics.put(categoryName, measurements);
      }
      measurements.addAll(category.getValue());
    }
  }

  private void printStatistics(String prefix, Map<String, List<Pair<Long, Long>>> statistics) {

    for (Map.Entry<String, List<Pair<Long, Long>>> entry : statistics.entrySet()) {

      String categoryName = entry.getKey();
      LOGGER.info(categoryName + ": " + entry);

      sortEntries(entry.getValue());

      List<String> lines = new ArrayList<String>();
      for (Pair<Long, Long> timeMeasurement : entry.getValue()) {
        lines.add(timeMeasurement.getLeft() + ";" + timeMeasurement.getRight());
      }

      try {
        Files.write(Paths.get(prefix + categoryName + ".csv"), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND,
            StandardOpenOption.CREATE);
      } catch (IOException ioe) {
        LOGGER.error("cannot write statistics: ", ioe);
      }
    }
  }


  private void sortEntries(List<Pair<Long, Long>> categoryStats) {

    Comparator<Pair<Long, Long>> longComparator = new Comparator<Pair<Long, Long>>() {

      @Override
      public int compare(Pair<Long, Long> o1, Pair<Long, Long> o2) {
        return Long.compare(o1.getLeft(), o2.getLeft());
      }
    };
    Collections.sort(categoryStats, longComparator);
  }

}
