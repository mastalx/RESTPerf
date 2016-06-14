package ch.tie.perf.scenario;

import java.nio.file.Files;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.Obj;


public class RunView extends StatisticsScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunView.class);


  private final RequestBroker rb;
  private final String menuLink;
  private final ScenarioRunner scenarioRunner;


  public RunView(String menuLink, RequestBroker rb, ScenarioRunner scenarioRunner) {
    this.menuLink = menuLink;
    this.rb = rb;
    this.scenarioRunner = scenarioRunner;

  }

  @Override
  public RunView call() throws Exception {
    try {

      String viewLink = getViewLink();
      LOGGER.debug("got view link:" + viewLink);

      Files.createDirectories(RunGetBytes.BINARIES_PATH);

      RunGetBytes viewPDFScenario = new RunGetBytes(viewLink, "GET_PDF", ".pdf", rb);
      Future<Scenario> viewPdfFuture = scenarioRunner.run(viewPDFScenario);
      addChildTask(viewPdfFuture);

      String thumbnailLink = viewLink + "?imageType=THUMBNAIL_M";
      RunGetBytes viewThumbnailScenario = new RunGetBytes(thumbnailLink, "GET_THUMBNAIL", ".jpg", rb);
      Future<Scenario> viewThumbnailFuture = scenarioRunner.run(viewThumbnailScenario);
      addChildTask(viewThumbnailFuture);

      LOGGER.debug("done view with link:" + viewLink);
    } catch (Exception e) {
      LOGGER.error("error in view:", e);
    }
    return this;
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
