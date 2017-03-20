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

  public ScenarioRunner(int parallelism) {
    LOGGER.info("startup Performance test with parallelism: " + parallelism);
    this.executorService = Executors.newWorkStealingPool(parallelism);
  }

  public void runAndWait(Scenario scenario) {
    try {
      getExecutorService().submit(scenario).get();
    } catch (InterruptedException | ExecutionException exception) {
      LOGGER.error(exception);
    }
  }

  public Future<Scenario> run(Scenario scenario) {
    return getExecutorService().submit(scenario);
  }

  private void shutDownPerformanceTest() {

    getExecutorService().shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!getExecutorService().awaitTermination(20, TimeUnit.SECONDS)) {
        getExecutorService().shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        getExecutorService().awaitTermination(20, TimeUnit.SECONDS);
      }
    } catch (InterruptedException ie) {
      LOGGER.error(ie);
      getExecutorService().shutdownNow();
    }
  }


  @Override
  public void close() throws IOException {
    shutDownPerformanceTest();
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }
}
