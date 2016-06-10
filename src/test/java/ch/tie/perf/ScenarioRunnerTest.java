package ch.tie.perf;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ch.tie.perf.scenario.RunSucher;
import ch.tie.perf.scenario.Scenario;

public class ScenarioRunnerTest {

  private static final Logger LOGGER = LogManager.getLogger(ScenarioRunnerTest.class);

  private final static String KONS_REST2 = "http://10.5.69.18:7501";

  @Test
  public void runFirstScenario() {

    ScenarioRunner scenarioRunner = new ScenarioRunner(2);
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
    });

    scenarioRunner.shutDownPerformanceTest();
  }

  @Test
  public void runSucherOnKons100() throws IOException {
    ScenarioRunner scenarioRunner = new ScenarioRunner(2);
    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3013955";
    RunSucher runSucher = new RunSucher(scenarioRunner, iengineUser, serviceUser, servicePassword, initialURI, pid);

    for (int i = 0; i < 10; i++) {
      scenarioRunner.runAndWait(runSucher);
    }

    scenarioRunner.shutDownPerformanceTest();


    StatisticsPrinter printer = new StatisticsPrinter();
    printer.printStatistics("100_", runSucher.getStatistics());

  }

  @Test
  public void runSucherOnKons300() throws IOException {
    ScenarioRunner scenarioRunner = new ScenarioRunner(2);
    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3540616";
    RunSucher runSucher = new RunSucher(scenarioRunner, iengineUser, serviceUser, servicePassword, initialURI, pid);

    for (int i = 0; i < 10; i++) {
      scenarioRunner.runAndWait(runSucher);
    }

    scenarioRunner.shutDownPerformanceTest();


    StatisticsPrinter printer = new StatisticsPrinter();
    printer.printStatistics("300_", runSucher.getStatistics());

  }

  @Test
  public void runSucherOnKons30() throws IOException {
    ScenarioRunner scenarioRunner = new ScenarioRunner(2);
    String iengineUser = "TIESUMSE";
    String serviceUser = "PAT_ARCHIVE_VIEWER_USER";
    String servicePassword = "Sonne123";
    String initialURI = KONS_REST2 + "/rest2/objects?q=PAV%20Dokumentenliste";
    String pid = "3555973";
    RunSucher runSucher = new RunSucher(scenarioRunner, iengineUser, serviceUser, servicePassword, initialURI, pid);

    for (int i = 0; i < 10; i++) {
      scenarioRunner.runAndWait(runSucher);
    }

    scenarioRunner.shutDownPerformanceTest();


    StatisticsPrinter printer = new StatisticsPrinter();
    printer.printStatistics("30_", runSucher.getStatistics());

  }
}
