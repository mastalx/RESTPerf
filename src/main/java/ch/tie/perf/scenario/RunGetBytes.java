package ch.tie.perf.scenario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.http.RequestBroker;

public class RunGetBytes extends AbstractScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunGetBytes.class);

  public static final Path BINARIES_PATH = Paths.get("binaries");
  private final String viewLink;
  private final String category;
  private final String fileExtension;

  private final RequestBroker rb;


  public RunGetBytes(String viewLink, String category, String fileExtension, RequestBroker requestBroker) {
    this.viewLink = viewLink;
    this.category = category;
    this.rb = requestBroker;
    this.fileExtension = fileExtension;
  }

  @Override
  public Scenario call() throws Exception {

    LOGGER.debug("start getting bytes on category:" + category + " with link: " + viewLink);
    byte[] pdfBytes = rb.doGet(viewLink, byte[].class, category);

    int lastIndexOfSlash = viewLink.lastIndexOf("/");
    String fileName = viewLink.substring(lastIndexOfSlash + 1);
    fileName = fileName.replace("?", "").replace("=", "") + UUID.randomUUID();
    Path temppdf = Paths.get(BINARIES_PATH.toString(), fileName + fileExtension);
    Files.write(temppdf, pdfBytes, StandardOpenOption.CREATE);

    LOGGER.debug("finished getting bytes on category:" + category + " with link: " + viewLink);

    return this;
  }
}
