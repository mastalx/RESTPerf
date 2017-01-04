package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.StatisticsCollector;
import ch.tie.perf.http.RequestBroker;

public class RunSucherTest {

  private final static String KONS_REST2 = "http://10.5.69.18:7501";

  @Test
  public void runSucherOnKons() throws Exception {

    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";

    Statistics stats = new Statistics();
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(4);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword, stats)) {
      RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);

      Future<Scenario> future = scenarioRunner.run(runSucher);

      StatisticsCollector statsHelper = new StatisticsCollector(stats);

      String experimentName = "runsucher_";
      List<Future<Scenario>> futures = new ArrayList<>();
      futures.add(future);
      statsHelper.waitForEndAndPrintStats(futures, experimentName);

    }
  }
}
