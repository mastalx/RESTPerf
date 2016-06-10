package ch.tie.perf.scenario;

import org.junit.Test;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.StatisticsPrinter;

public class RunSucherTest {


  private final static String KONS_REST2 = "http://10.5.69.18:7501";


  @Test
  public void runSucherOnKons() throws Exception {
    ScenarioRunner scenarioRunner = new ScenarioRunner(2);
    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";
    RunSucher runSucher = new RunSucher(scenarioRunner, iengineUser, serviceUser, servicePassword, initialURI, pid);

    runSucher.call();

    StatisticsPrinter printer = new StatisticsPrinter();
    printer.printStatistics("runsucher_", runSucher.getStatistics());


  }


}
