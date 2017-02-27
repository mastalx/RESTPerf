package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.ScenarioRunnerTest;
import ch.tie.perf.StatisticsCollector;
import ch.tie.perf.http.RequestBroker;

public class RunSucherTest {


  @Test
  public void runSucherOnKons() throws Exception {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = ScenarioRunnerTest.BACKEND + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";
    boolean saveFile = false;
    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(100);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats,
            ScenarioRunnerTest.CLIENT_IP)) {

      RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb, saveFile);

      Future<Scenario> future = scenarioRunner.run(runSucher);

      StatisticsCollector statsHelper = new StatisticsCollector(stats);

      String experimentName = "runsucher_";
      List<Future<Scenario>> futures = new ArrayList<>();
      futures.add(future);
      statsHelper.waitForEndAndPrintStats(futures, experimentName);

    }
  }
}
