package ch.tie.perf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.scenario.RunSucher;
import ch.tie.perf.scenario.Scenario;

public class ScenarioRunnerTest {


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
        public Map<String, Map<Long, Long>> getStatistics() {
          // TODO Auto-generated method stub
          return null;
        }

        @Override
        public List<Future<? extends Scenario>> getSpawnedTasks() {
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
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(10);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {
      RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);

      List<Future<? extends Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 300; i++) {
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }


      StatisticsHelper statsHelper = new StatisticsHelper();
      ConcurrentHashMap<String, Map<Long, Long>> mergedStatistics = statsHelper.mergeStatistics(taskList);


      StatisticsHelper printer = new StatisticsHelper();
      printer.printStatistics("3x100_", mergedStatistics);

    }

  }

  @Test
  public void runSucherOnKons300() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3540616";
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(10);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {
      RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);

      List<Future<? extends Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 100; i++) {
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }


      StatisticsHelper statsHelper = new StatisticsHelper();
      ConcurrentHashMap<String, Map<Long, Long>> mergedStats = statsHelper.mergeStatistics(taskList);

      statsHelper.printStatistics("1x300_", mergedStats);
    }

  }

  @Test
  public void runSucherOnKons30() throws IOException, InterruptedException, ExecutionException {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(10);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {
      RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);

      List<Future<? extends Scenario>> taskList = new ArrayList<>();
      for (int i = 0; i < 1000; i++) {
        Future<Scenario> task = scenarioRunner.run(runSucher);
        taskList.add(task);
      }

      StatisticsHelper statsHelper = new StatisticsHelper();
      ConcurrentHashMap<String, Map<Long, Long>> mergedStats = statsHelper.mergeStatistics(taskList);
      statsHelper.printStatistics("10x30_", mergedStats);
    }

  }
}
