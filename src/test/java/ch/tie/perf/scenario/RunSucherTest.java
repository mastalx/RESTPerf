package ch.tie.perf.scenario;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.junit.Test;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.StatisticsHelper;
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
    try (ScenarioRunner scenarioRunner = new ScenarioRunner(4);
        RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {
      RunSucher runSucher = new RunSucher(scenarioRunner, initialURI, pid, rb);

      Future<Scenario> future = scenarioRunner.run(runSucher);

      StatisticsHelper statsHelper = new StatisticsHelper();
      @SuppressWarnings("unchecked")
      ConcurrentHashMap<String, Map<Long, Long>> mergedStatistics = statsHelper
          .mergeStatistics(Arrays.asList(new Future[]{future}));
      statsHelper.printStatistics("runsucher_", mergedStatistics);

    }

  }


}
