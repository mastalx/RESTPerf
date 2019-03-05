package ch.tie.perf.scenario;

import static java.nio.file.Files.lines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.WadoInstance;


public class RunWado extends AbstractScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunWado.class);

  private static final Path BINARIES_PATH = Paths.get("binaries");

  static {
    try {
      Files.createDirectories(BINARIES_PATH);
    } catch (IOException e) {
      LOGGER.error("could not create binaries folder: " + BINARIES_PATH, e);
    }
  }

  private final ScenarioRunner scenarioRunner;
  private final RequestBroker rb;
  private final String endpoint;
  private final String inputData;
  private final boolean saveFile;

  public RunWado(final ScenarioRunner scenarioRunner, final RequestBroker rb, final String endpoint, final String inputData, boolean saveFile) {
    this.scenarioRunner = scenarioRunner;
    this.rb = rb;
    this.endpoint = endpoint;
    this.inputData = inputData;
    this.saveFile = saveFile;
  }


  @Override
  public RunWado call() {
    for (WadoInstance image : readTestInstances(inputData)) {

      RunGetWadoInstance runWado = new RunGetWadoInstance(scenarioRunner, endpoint, image, rb, saveFile);

      Future<Scenario> future = scenarioRunner.run(runWado);
      addChildTask(future);
    }
    return this;

  }


  private List<WadoInstance> readTestInstances(final String inputData) {
    try (Stream<String> stream = lines(Paths.get(inputData))) {
      return stream.map(line -> {
        String[] str = line.split(",");
        return new WadoInstance(str[0], str[1], str[2]);
      }).collect(Collectors.toList());
    } catch (IOException e) {
      LOGGER.error("error in getting wado-instances.csv", e);
    }
    return Collections.emptyList();
  }


}
