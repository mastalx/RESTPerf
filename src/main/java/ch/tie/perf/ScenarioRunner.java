package ch.tie.perf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.scenario.Scenario;

public class ScenarioRunner implements AutoCloseable {

  private static final Logger LOGGER = LogManager.getLogger(ScenarioRunner.class);
  private final ExecutorService executorService;

  public ScenarioRunner(int parallelism) {
    LOGGER.info("startup Performance test with parallelism: {}", parallelism);
    executorService = Executors.newWorkStealingPool(parallelism);
  }

  public Future<Scenario> run(Scenario scenario) {
    return getExecutorService().submit(scenario);
  }

  // from  https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
  private void shutdownAndAwaitTermination(ExecutorService pool) {
    pool.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!pool.awaitTermination(20, TimeUnit.SECONDS)) {
        pool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!pool.awaitTermination(20, TimeUnit.SECONDS)) {
          LOGGER.error("Pool did not terminate");
        }
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      pool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }


  @Override
  public void close() {
    shutdownAndAwaitTermination(getExecutorService());
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }
}
