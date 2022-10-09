package ch.tie.perf;

import static ch.tie.perf.MainPerf.ENDPOINT_OPT;
import static ch.tie.perf.MainPerf.EXPERIMENT_NAME_OPT;
import static ch.tie.perf.MainPerf.SAVE_FILE_OPT;
import static ch.tie.perf.MainPerf.THREADS_OPT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.scenario.RunSucher;
import ch.tie.perf.scenario.Scenario;
import ch.tie.perf.statistic.Statistics;
import ch.tie.perf.statistic.StatisticsCollector;

public class RestPerfMain {

  private static final Logger LOGGER = LogManager.getLogger(RestPerfMain.class);

  public static final String REST2_EXPERIMENT = "runsucher_";

  public static void main(String[] args) {

    Options options = MainPerf.initMainOptions();

    Option pidOpt = Option.builder("pid").hasArg().required().desc("pid - patient id").build();
    Option restUserOpt = Option.builder("us").longOpt("rest-user").hasArg().required().desc("rest2 user").build();
    Option restPasswordOpt = Option.builder("pw")
        .longOpt("rest-password")
        .hasArg()
        .required()
        .desc("rest2 password")
        .build();

    Option iengineUserOpt = Option.builder("user").longOpt("iengine-user").hasArg().desc("i-engine user").build();
    Option numberOfTestopt = Option.builder("r")
        .longOpt("totalruns")
        .hasArg()
        .type(Integer.class)
        .desc("number of test runs")
        .build();

    options.addOption(restUserOpt);
    options.addOption(restPasswordOpt);
    options.addOption(iengineUserOpt);
    options.addOption(pidOpt);
    options.addOption(numberOfTestopt);

    // create the parser
    CommandLineParser parser = new DefaultParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse(options, args);

      final int numberOfThreads = Integer.parseInt(line.getOptionValue(THREADS_OPT.getOpt(), "1"));
      final String endpoint = line.getOptionValue(ENDPOINT_OPT.getOpt());
      final boolean saveFile = Boolean.parseBoolean(line.getOptionValue(SAVE_FILE_OPT.getOpt(), "false"));
      final String experimentName = line.getOptionValue(EXPERIMENT_NAME_OPT.getOpt(), REST2_EXPERIMENT);

      final String pid = line.getOptionValue(pidOpt.getOpt());
      final String restUser = line.getOptionValue(restUserOpt.getOpt());
      final String restPassword = line.getOptionValue(restPasswordOpt.getOpt());
      final String iengineUser = line.getOptionValue(iengineUserOpt.getOpt(), "");
      final int numberOfTests = Integer.parseInt(line.getOptionValue(numberOfTestopt.getOpt(), "10"));

      LOGGER.info("STARTING EXPERIMENT {}", experimentName);
      long startTime = System.currentTimeMillis();

      Statistics stats = new Statistics();
      try (ScenarioRunner scenarioRunner = new ScenarioRunner(numberOfThreads);
          RequestBroker rb = new RequestBroker(iengineUser, restUser, restPassword, stats)) {

        StatisticsCollector statsHelper = new StatisticsCollector(stats);

        List<Future<Scenario>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfTests; i++) {
          RunSucher runSucher = new RunSucher(scenarioRunner, endpoint, pid, rb, saveFile);
          Future<Scenario> future = scenarioRunner.run(runSucher);
          futures.add(future);
        }

        statsHelper.waitForEndAndPrintStats(futures, experimentName);

      } catch (IOException e) {
        LOGGER.error("Connection failed.  Reason: {}", e.getMessage());
      }

      LOGGER.info("FINISHED EXPERIMENT {} took: {}ms", experimentName, (System.currentTimeMillis() - startTime));

    } catch (ParseException exp) {
      // oops, something went wrong
      LOGGER.error("Parsing failed.  Reason: {}", exp.getMessage());

      HelpFormatter formatterx = new HelpFormatter();
      formatterx.printHelp("RestPerf", options);
    }
  }


}
