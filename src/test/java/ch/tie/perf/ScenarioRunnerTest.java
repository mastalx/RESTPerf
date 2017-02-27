package ch.tie.perf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.scenario.RunSucher;
import ch.tie.perf.scenario.Scenario;
import ch.tie.perf.scenario.Statistics;

public class ScenarioRunnerTest {

  private static final Logger LOGGER = LogManager.getLogger(ScenarioRunnerTest.class);

  public final static String BACKEND = "http://10.5.68.215:7501";
  public final static String CLIENT_IP = "foobar";

  private static final boolean SAVE_FILE = false;

  private static final int MULTIPLIER = 1;

  @Test
  public void runFirstScenario() throws IOException {

    try (ScenarioRunner scenarioRunner = new ScenarioRunner(2)) {
      scenarioRunner.runAndWait(new Scenario() {


        @Override
        public Scenario call() throws Exception {
          // TODO Auto-generated method stub
          return null;
        }

        @Override
        public List<Future<Scenario>> getSpawnedTasks() {
          // TODO Auto-generated method stub
          return null;
        }
      });
    }
  }

  @Test
  public void runSucherOnKons100() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3013955";
    String experimentName = "3x100_";

    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(100);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {

      List<Future<Scenario>> taskList = new ArrayList<>();

      for (int i = 0; i < 3 * MULTIPLIER; i++) {
        RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb, SAVE_FILE);
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }

      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      statsHelper.waitForEndAndPrintStats(taskList, experimentName);
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
    String experimentName = "1x300_";
    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(100);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {

      List<Future<Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 1 * MULTIPLIER; i++) {
        RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb, SAVE_FILE);
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }

      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      statsHelper.waitForEndAndPrintStats(taskList, experimentName);
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
    String experimentName = "10x30";
    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(100);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats, CLIENT_IP)) {

      List<Future<Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 10 * MULTIPLIER; i++) {
        RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb, SAVE_FILE);
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }

      StatisticsCollector statsHelper = new StatisticsCollector(stats);
      statsHelper.waitForEndAndPrintStats(taskList, experimentName);
    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName);

  }
}
