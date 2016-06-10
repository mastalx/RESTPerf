package ch.tie.perf.scenario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.Obj;


public class RunView extends StatisticsScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunView.class);

  private final RequestBroker rb;
  private final String menuLink;

  public RunView(String menuLink, RequestBroker rb) {
    this.menuLink = menuLink;
    this.rb = rb;

  }

  @Override
  public RunView call() throws Exception {


    String viewLink = getViewLink();

    Files.createDirectories(Paths.get("binaries"));


    byte[] pdfBytes = getBytes(viewLink, "GET_PDF");
    Path temppdf = Files.createTempFile(Paths.get("binaries"), "", ".pdf");
    Files.write(temppdf, pdfBytes);


    String thumbnailLink = viewLink + "?imageType=THUMBNAIL_M";
    byte[] thumbnailBytes = getBytes(thumbnailLink, "GET_THUMBNAIL");
    Path tempjpg = Files.createTempFile(Paths.get("binaries"), "", ".jpg");
    Files.write(tempjpg, thumbnailBytes);

    return this;
  }


  private byte[] getBytes(String viewLink, String key) {

    try {
      long start = System.nanoTime();
      byte[] bytes = rb.doGet(viewLink, byte[].class);
      long durationGetBytes = System.nanoTime() - start;
      updateStatistics(durationGetBytes, key);
      return bytes;
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return new byte[]{};
  }

  private String getViewLink() {
    long start = System.nanoTime();
    Obj menu = rb.doGet(menuLink, Obj.class);
    long durationGetMenu = System.nanoTime() - start;
    updateStatistics(durationGetMenu, "GET_DOCUMENT_MENU");
    String viewLink = menu.getLink("VIEW").getHref();
    return viewLink;
  }


}
