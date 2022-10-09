package ch.tie.perf.scenario;

import ch.tie.perf.http.RequestBroker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class RunGetBytes extends AbstractScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunGetBytes.class);

  private static final Path BINARIES_PATH = Paths.get("binaries");

  static {
    try {
      Files.createDirectories(RunGetBytes.BINARIES_PATH);
    } catch (IOException e) {
      LOGGER.error("could not create binaries folder: " + BINARIES_PATH, e);
    }
  }
  private final String viewLink;
  private final String category;
  private final RequestBroker rb;
  private final boolean saveFile;


  public RunGetBytes(String viewLink, String category, RequestBroker requestBroker, boolean saveFile) {
    this.viewLink = viewLink;
    this.category = category;
    this.rb = requestBroker;
    this.saveFile = saveFile;
  }

  @Override
  public Scenario call() {
    try {
      LOGGER.debug("start getting bytes on category:" + category + " with link: " + viewLink);
      FileHolder file = rb.doGet(viewLink, FileHolder.class, category);

      if (saveFile) {
        Path temppdf = Paths.get(BINARIES_PATH.toString(), file.getFileName());
        Files.write(temppdf, file.getBytes(), StandardOpenOption.CREATE);
      }

      LOGGER.debug("finished getting bytes on category:" + category + " with link: " + viewLink);
    } catch (Exception e) {
      LOGGER.error("error in getting bytes", e);
    }
    return this;
  }
}
