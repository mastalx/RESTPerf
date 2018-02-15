package ch.tie.perf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.scenario.Measurement;
import ch.tie.perf.scenario.Scenario;
import ch.tie.perf.scenario.Statistics;

public class StatisticsCollector {

  private static final Logger LOGGER = LogManager.getLogger(StatisticsCollector.class);
  public final DateTimeFormatter filenamePrefixFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private final Statistics stats;


  public StatisticsCollector(Statistics stats) {
    this.stats = stats;
  }

  public void waitForEndAndPrintStats(List<Future<Scenario>> tasks, String experiment) {

    String fileNamePrefix = LocalDateTime.now().format(filenamePrefixFormat) + "_" + experiment + "_";

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


  public void printStatistics(String prefix) {
    stats.getMeasurements()
        .stream()
        .collect(Collectors.groupingBy(Measurement::getName))
        .forEach((name, measurements) -> {
          Path path = Paths.get(prefix + name + ".csv");
          try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND,
              StandardOpenOption.CREATE); PrintWriter pw = new PrintWriter(writer)) {
            measurements.stream()
                .sorted()
                .map(measurement -> measurement.getName() + ";" + measurement.getId() + ";" + measurement.getTimestamp()
                    + ";" + measurement.getDuration())
                .forEach(pw::println);
          } catch (IOException ioe) {
            LOGGER.error("cannot write statistics: ", ioe);
          }
        });
  }
}
