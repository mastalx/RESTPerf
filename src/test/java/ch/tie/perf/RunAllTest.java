package ch.tie.perf;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.scenario.RunAll;
import ch.tie.perf.scenario.Statistics;

@Ignore
public class RunAllTest {

  private static final Logger LOGGER = LogManager.getLogger(ScenarioRunnerTest.class);

  public final static String BACKEND = "http://10.5.69.18:7501";
  public final static String CLIENT_IP = "foobar";

  private static final boolean SAVE_FILE = false;

  private static final int MULTIPLIER = 5;


  @Test
  public void runSucherOnKons100() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3013955";
    String experimentName = "RUN_ALL_3x100_";

    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(100);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {


      IntStream.range(0, 3 * MULTIPLIER)
          .parallel()
          .mapToObj(i -> new RunAll(scenarioRunner.getExecutorService(), initialURI, pid, rb, SAVE_FILE))
          .map(r -> r.run())
          .map(CompletableFuture::join)
          .collect(Collectors.toList());

      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      Date now = new Date();
      String fileNamePrefix = statsHelper.filenamePrefixFormat.format(now) + "_" + experimentName + "_";
      statsHelper.printStatistics(fileNamePrefix);

    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName);

  }

  @Test
  public void runSucherOnKons300() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3540616";
    String experimentName = "RUN_ALL_1x300_";
    LOGGER.info("STARTING EXPERIMENT" + experimentName);

    Statistics stats = new Statistics();

    try (ScenarioRunner scenarioRunner = new ScenarioRunner(100);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {


      IntStream.range(0, 1 * MULTIPLIER)
          .parallel()
          .mapToObj(i -> new RunAll(scenarioRunner.getExecutorService(), initialURI, pid, rb, SAVE_FILE))
          .map(r -> r.run())
          .map(CompletableFuture::join)
          .collect(Collectors.toList());

      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      Date now = new Date();
      String fileNamePrefix = statsHelper.filenamePrefixFormat.format(now) + "_" + experimentName + "_";
      statsHelper.printStatistics(fileNamePrefix);
    }


    LOGGER.info("FINISHED EXPERIMENT" + experimentName);

  }

  @Test
  public void runSucherOnKons30() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";
    String experimentName = "RUN_ALL_10x30";
    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(100);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {


      IntStream.range(0, 10 * MULTIPLIER)
          .parallel()
          .mapToObj(i -> new RunAll(scenarioRunner.getExecutorService(), initialURI, pid, rb, SAVE_FILE))
          .map(r -> r.run())
          .map(CompletableFuture::join)
          .collect(Collectors.toList());

      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      Date now = new Date();
      String fileNamePrefix = statsHelper.filenamePrefixFormat.format(now) + "_" + experimentName + "_";
      statsHelper.printStatistics(fileNamePrefix);
    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName);

  }
}
