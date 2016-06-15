package ch.tie.perf.scenario;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.Link;

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

      Files.createDirectories(RunGetBytes.BINARIES_PATH);
      Obj menu = getMenu();
      String viewLink = menu.getLink("VIEW").getHref();

      LOGGER.debug("got view link:" + viewLink);

      getPDF(viewLink);
      getOldThumnbail(viewLink);
      getNewThumbnail(menu);


      LOGGER.debug("done view with link:" + viewLink);
    } catch (Exception e) {
      LOGGER.error("error in view:", e);
    }
    return this;
  }

  private void getNewThumbnail(Obj menu) {
    Link newThumbnailLink = menu.getLink("VIEW_THUMBNAIL");
    if (newThumbnailLink != null) {
      String thumbnailLink = newThumbnailLink.getHref();
      thumbnailLink = thumbnailLink + "?imageType=THUMBNAIL_M";
      LOGGER.debug("got thumbnail link without protocol:" + thumbnailLink);
      RunGetBytes viewThumbnailScenario = new RunGetBytes(thumbnailLink, "GET_THUMBNAIL_NO_PROTOCOL", ".jpg", rb);
      Future<Scenario> viewThumbnailFuture = scenarioRunner.run(viewThumbnailScenario);
      addChildTask(viewThumbnailFuture);
    }
  }


  private void getOldThumnbail(String viewLink) {
    String thumbnailLink = viewLink + "?imageType=THUMBNAIL_M";
    LOGGER.debug("got thumbnail link with protocol:" + thumbnailLink);
    RunGetBytes viewThumbnailScenario = new RunGetBytes(thumbnailLink, "GET_THUMBNAIL", ".jpg", rb);
    Future<Scenario> viewThumbnailFuture = scenarioRunner.run(viewThumbnailScenario);
    addChildTask(viewThumbnailFuture);
  }

  private String getPDF(String viewLink) throws IOException {
    RunGetBytes viewPDFScenario = new RunGetBytes(viewLink, "GET_PDF", ".pdf", rb);
    Future<Scenario> viewPdfFuture = scenarioRunner.run(viewPDFScenario);
    addChildTask(viewPdfFuture);
    return viewLink;
  }


  private Obj getMenu() {
    long start = System.nanoTime();
    Obj menu = rb.doGet(menuLink, Obj.class);
    long durationGetMenu = System.nanoTime() - start;
    updateStatistics(durationGetMenu, "GET_DOCUMENT_MENU");
    return menu;
  }

}
