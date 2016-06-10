package ch.tie.perf;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.scenario.Scenario;

public class ScenarioRunner implements Closeable {

  private static final Logger LOGGER = LogManager.getLogger(ScenarioRunner.class);
  private final ExecutorService executorService;

  public ScenarioRunner(int concurrency) {
    LOGGER.info("startup Performance test with concurrency: " + concurrency);
    executorService = Executors.newFixedThreadPool(concurrency);
  }


  public void runAndWait(Scenario scenario) {
    try {
      executorService.submit(scenario).get();
    } catch (InterruptedException | ExecutionException exception) {
      LOGGER.error(exception);
    }
  }

  public Future<Scenario> run(Scenario scenario) {
    return executorService.submit(scenario);
  }


  private void shutDownPerformanceTest() {

    executorService.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!executorService.awaitTermination(20, TimeUnit.SECONDS)) {
        executorService.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        executorService.awaitTermination(20, TimeUnit.SECONDS);
      }
    } catch (InterruptedException ie) {
      LOGGER.error(ie);
      executorService.shutdownNow();
    }
  }


  @Override
  public void close() throws IOException {
    shutDownPerformanceTest();
  }
}
