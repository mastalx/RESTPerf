package ch.tie.perf.scenario;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.WadoInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class RunGetWadoInstance extends AbstractScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunGetWadoInstance.class);

  private static final Path BINARIES_PATH = Paths.get("binaries");

  static {
    try {
      Files.createDirectories(BINARIES_PATH);
    } catch (IOException e) {
      LOGGER.error("could not create binaries folder: " + BINARIES_PATH, e);
    }
  }

  private final ScenarioRunner scenarioRunner;
  private final String initialURI;
  private final WadoInstance instance;
  private final RequestBroker rb;

  private final boolean saveFile;

  public RunGetWadoInstance(ScenarioRunner scenarioRunner,
      String initialURI,
      WadoInstance instance,
      RequestBroker rb,
      boolean saveFile) {
    this.scenarioRunner = scenarioRunner;
    this.initialURI = initialURI;
    this.instance = instance;
    this.rb = rb;
    this.saveFile = saveFile;
  }


  @Override
  public RunGetWadoInstance call() {

    final String wadoLink = String.format(
          "%s?requestType=WADO&contentType=application/dicom&studyUID=%s&seriesUID=%s1&objectUID=%s", initialURI,
        instance.getStudyInstanceUid(), instance.getSeriesInstanceUid(), instance.getSopInstanceUid());

    try {
      LOGGER.debug("start getting bytes on image with link: {}", wadoLink);
      FileHolder file = rb.doGet(wadoLink, FileHolder.class, instance.getStudyInstanceUid());

      if (saveFile) {
        Path temppdf = Paths.get(BINARIES_PATH.toString(), file.getFileName());
        Files.write(temppdf, file.getBytes(), StandardOpenOption.CREATE);
      }

      LOGGER.debug("finished getting bytes on image with link: {}", wadoLink);
    } catch (Exception e) {
      LOGGER.error("error in getting bytes", e);
    }
    return this;

  }


}
