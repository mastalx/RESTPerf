package ch.tie.perf;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.scenario.RunAll;
import ch.tie.perf.scenario.Statistics;


public class RunAllTest {

  private static final int PARALLELISM = 80;

  private static final Logger LOGGER = LogManager.getLogger(RunAllTest.class);

  private final static String BACKEND = "http://10.5.69.18:7501";
  private final static String CLIENT_IP = "foobar";

  private static final boolean SAVE_FILE = false;

  private static final int MULTIPLIER = 5;

  @Test
  public void runSucherOnKons100() throws IOException {


    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3013955";
    String experimentName = "RUN_ALL_3x100_COMPLETABLE_FUTURE_";
    long startTime = System.currentTimeMillis();
    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(PARALLELISM);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {

      IntStream.range(0, 3 * MULTIPLIER)
          .parallel()
          .mapToObj(i -> new RunAll(scenarioRunner.getExecutorService(), initialURI, pid, rb, SAVE_FILE))
          .flatMap(RunAll::run)
          .collect(toList())
          .forEach(CompletableFuture::join);

      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      String fileNamePrefix = now().format(statsHelper.filenamePrefixFormat) + "_" + experimentName + "_";
      statsHelper.printStatistics(fileNamePrefix);

    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName + " took: " + (System.currentTimeMillis() - startTime) + "ms");

  }

  @Test
  public void runSucherOnKons300() throws IOException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3540616";
    String experimentName = "RUN_ALL_1x300_COMPLETABLE_FUTURE_";
    long startTime = System.currentTimeMillis();
    LOGGER.info("STARTING EXPERIMENT" + experimentName);

    Statistics stats = new Statistics();

    try (ScenarioRunner scenarioRunner = new ScenarioRunner(PARALLELISM);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {

      IntStream.range(0, MULTIPLIER)
          .parallel()
          .mapToObj(i -> new RunAll(scenarioRunner.getExecutorService(), initialURI, pid, rb, SAVE_FILE))
          .flatMap(RunAll::run)
          .collect(toList())
          .forEach(CompletableFuture::join);
      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      String fileNamePrefix = now().format(statsHelper.filenamePrefixFormat) + "_" + experimentName + "_";
      statsHelper.printStatistics(fileNamePrefix);
    }


    LOGGER.info("FINISHED EXPERIMENT" + experimentName + " took: " + (System.currentTimeMillis() - startTime) + "ms");

  }

  @Test
  public void runSucherOnKons30() throws IOException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";
    String experimentName = "RUN_ALL_10x30_COMPLETABLE_FUTURE_";
    long startTime = System.currentTimeMillis();
    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(PARALLELISM);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {


      IntStream.range(0, 10 * MULTIPLIER)
          .parallel()
          .mapToObj(i -> new RunAll(scenarioRunner.getExecutorService(), initialURI, pid, rb, SAVE_FILE))
          .flatMap(RunAll::run)
          .collect(toList())
          .forEach(CompletableFuture::join);


      StatisticsCollector statsHelper = new StatisticsCollector(stats);

      String fileNamePrefix = now().format(statsHelper.filenamePrefixFormat) + "_" + experimentName + "_";
      statsHelper.printStatistics(fileNamePrefix);
    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName + " took: " + (System.currentTimeMillis() - startTime) + "ms");

  }
}
