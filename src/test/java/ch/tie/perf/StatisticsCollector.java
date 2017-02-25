package ch.tie.perf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.scenario.Scenario;
import ch.tie.perf.scenario.Statistics;

public class StatisticsCollector {

  private static final Logger LOGGER = LogManager.getLogger(StatisticsCollector.class);
  private final SimpleDateFormat filenamePrefixFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  private final Statistics stats;


  public StatisticsCollector(Statistics stats) {
    this.stats = stats;
  }


  public void waitForEndAndPrintStats(List<Future<Scenario>> tasks, String experiment) {
    Date now = new Date();
    String fileNamePrefix = filenamePrefixFormat.format(now) + "_" + experiment + "_";

    waitForEndOfTasks(tasks);
    printStatistics(fileNamePrefix);
  }


  private void waitForEndOfTasks(List<Future<Scenario>> tasks) {
    Deque<Future<Scenario>> queue = new LinkedList<>(tasks);

    while (!queue.isEmpty()) {
      Future<Scenario> task = queue.pollFirst();
      try {
        Scenario scenario = task.get();
        List<Future<Scenario>> spawnedTasks = scenario.getSpawnedTasks();
        queue.addAll(spawnedTasks);
      } catch (InterruptedException | ExecutionException e) {
        LOGGER.error("error while getting task result", e);
      }
    }
  }


  private void printStatistics(String prefix) {

    stats.forEach((name, measurements) -> {
      List<String> lines = measurements.stream()
          .sorted()
          .map(
              measurement -> measurement.getName() + ";" + measurement.getTimestamp() + ";" + measurement.getDuration())
          .collect(Collectors.toList());


      try {
        Files.write(Paths.get(prefix + name + ".csv"), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND,
            StandardOpenOption.CREATE);
      } catch (IOException ioe) {
        LOGGER.error("cannot write statistics: ", ioe);
      }
    });
  }
}
