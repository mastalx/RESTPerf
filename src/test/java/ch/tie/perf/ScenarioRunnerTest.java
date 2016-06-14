package ch.tie.perf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.scenario.Pair;
import ch.tie.perf.scenario.RunSucher;
import ch.tie.perf.scenario.Scenario;

public class ScenarioRunnerTest {


  private static final Logger LOGGER = LogManager.getLogger(ScenarioRunnerTest.class);

  private final static String KONS_REST2 = "http://10.5.69.18:7501";

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
        public Map<String, List<Pair<Long, Long>>> getStatistics() {
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
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3013955";
    String experimentName = "30x100_";

    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(10);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {

      List<Future<Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 30; i++) {
        RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }

      StatisticsCollector statsHelper = new StatisticsCollector();
      statsHelper.collectAndPrintStatistics(taskList, experimentName);
    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName);

  }

  @Test
  public void runSucherOnKons300() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3540616";
    String experimentName = "10x300_";
    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(10);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {

      List<Future<Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }

      StatisticsCollector statsHelper = new StatisticsCollector();
      statsHelper.collectAndPrintStatistics(taskList, experimentName);
    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName);

  }

  @Test
  public void runSucherOnKons30() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";
    String experimentName = "100x30";
    LOGGER.info("STARTING EXPERIMENT" + experimentName);
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(10);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {

      List<Future<Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }

      StatisticsCollector statsHelper = new StatisticsCollector();
      statsHelper.collectAndPrintStatistics(taskList, experimentName);
    }
    LOGGER.info("FINISHED EXPERIMENT" + experimentName);

  }
}
