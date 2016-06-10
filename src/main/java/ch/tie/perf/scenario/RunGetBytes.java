package ch.tie.perf.scenario;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.http.RequestBroker;

public class RunGetBytes extends StatisticsScenario {

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
    byte[] pdfBytes = getBytes(viewLink, category);

    int lastIndexOfSlash = viewLink.lastIndexOf("/");
    String fileName = viewLink.substring(lastIndexOfSlash + 1);
    fileName = fileName.replace("?", "").replace("=", "") + UUID.randomUUID();
    Path temppdf = Paths.get(BINARIES_PATH.toString(), fileName + fileExtension);

    try (OutputStream os = new FileOutputStream(temppdf.toFile())) {
      IOUtils.write(pdfBytes, os);
    }

    LOGGER.debug("finished getting bytes on category:" + category + " with link: " + viewLink);

    return this;
  }


  private byte[] getBytes(String viewLink, String key) {
    long start = System.nanoTime();
    byte[] bytes = rb.doGet(viewLink, byte[].class);
    long durationGetBytes = System.nanoTime() - start;
    updateStatistics(durationGetBytes, key);
    return bytes;
  }

  @Override
  public List<Future<? extends Scenario>> getSpawnedTasks() {
    return new ArrayList<Future<? extends Scenario>>();
  }
}
