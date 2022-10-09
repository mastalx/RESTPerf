package ch.tie.perf;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.scenario.RunWado;
import ch.tie.perf.scenario.Scenario;
import ch.tie.perf.statistic.Statistics;
import ch.tie.perf.statistic.StatisticsCollector;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static ch.tie.perf.MainPerf.*;

public class WadoPerfMain {

  private static final Logger LOGGER = LogManager.getLogger(WadoPerfMain.class);

  private static final String WADO_EXPERIMENT = "wadoperf_";
  private static final String DEFAULT_INPUT_DATA = "./wado-instances.csv";

  public static void main(String[] args) {
    Options options = MainPerf.initMainOptions();

    Option inputDataOpt = Option.builder("i")
        .longOpt("input")
        .hasArg()
        .required()
        .desc("file input for csv - studyUid,seriesUid,objectUid)")
        .build();

    options.addOption(inputDataOpt);

    // create the parser
    CommandLineParser parser = new DefaultParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse(options, args);

      final int numberOfThreads = Integer.parseInt(line.getOptionValue(THREADS_OPT.getOpt(), "1"));
      final String endpoint = line.getOptionValue(ENDPOINT_OPT.getOpt());
      final boolean saveFile = Boolean.parseBoolean(line.getOptionValue(SAVE_FILE_OPT.getOpt(), "false"));
      final String experimentName = line.getOptionValue(EXPERIMENT_NAME_OPT.getOpt(), WADO_EXPERIMENT);

      final String inputData = line.getOptionValue(inputDataOpt.getOpt(), DEFAULT_INPUT_DATA);

      LOGGER.info("STARTING EXPERIMENT {}", experimentName);
      long startTime = System.currentTimeMillis();

      Statistics stats = new Statistics();
      try (ScenarioRunner scenarioRunner = new ScenarioRunner(numberOfThreads);
          RequestBroker rb = new RequestBroker(stats)) {

        List<Future<Scenario>> futures = new ArrayList<>();
        StatisticsCollector statsHelper = new StatisticsCollector(stats);

        RunWado wado = new RunWado(scenarioRunner, rb, endpoint, inputData, saveFile);
        Future<Scenario> future = scenarioRunner.run(wado);
        futures.add(future);

        statsHelper.waitForEndAndPrintStats(futures, experimentName);

      } catch (IOException e) {
        LOGGER.error("Connection failed.  Reason: {}", e.getMessage());
      }

      LOGGER.info("FINISHED EXPERIMENT {} took: {}ms", experimentName, (System.currentTimeMillis() - startTime));

    } catch (ParseException exp) {
      LOGGER.error("Parsing failed.  Reason: {}", exp.getMessage());

      HelpFormatter formatterx = new HelpFormatter();
      formatterx.printHelp("WadoPerf", options);
    }


  }


}
