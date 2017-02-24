package ch.tie.perf.scenario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.http.RequestBroker;

public class RunGetBytes extends AbstractScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunGetBytes.class);

  public static final Path BINARIES_PATH = Paths.get("binaries");
  private final String viewLink;
  private final String category;

  private final RequestBroker rb;


  public RunGetBytes(String viewLink, String category, RequestBroker requestBroker) {
    this.viewLink = viewLink;
    this.category = category;
    this.rb = requestBroker;
  }

  @Override
  public Scenario call() throws Exception {

    LOGGER.debug("start getting bytes on category:" + category + " with link: " + viewLink);
    FileHolder file = rb.doGet(viewLink, FileHolder.class, category);


    Path temppdf = Paths.get(BINARIES_PATH.toString(), file.getFileName());
    Files.write(temppdf, file.getBytes(), StandardOpenOption.CREATE);

    LOGGER.debug("finished getting bytes on category:" + category + " with link: " + viewLink);

    return this;
  }
}
