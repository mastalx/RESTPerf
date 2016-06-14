package ch.tie.perf.scenario;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.Obj;


public class RunSucher extends StatisticsScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunSucher.class);

  private final ScenarioRunner scenarioRunner;
  private final String initialURI;
  private final String pid;
  private final RequestBroker requestBroker;

  public RunSucher(ScenarioRunner scenarioRunner, String initialURI, String pid, RequestBroker requestBroker) {
    this.scenarioRunner = scenarioRunner;
    this.initialURI = initialURI;
    this.pid = pid;
    this.requestBroker = requestBroker;
  }


  @Override
  public RunSucher call() throws Exception {
    LOGGER.debug("running Sucher");

    try {
      String suchenLink = getSuchenLink(requestBroker);
      Obj dokumentenliste = doSearch(requestBroker, suchenLink);

      getThumnbailsAndPDFs(requestBroker, dokumentenliste);
    } catch (Exception e) {
      LOGGER.error("error in sucher", e);
    }

    return this;
  }


  private Obj doSearch(RequestBroker rb, String suchenLink) {

    Obj body = new Obj();
    Map<String, Object> attributes = new HashMap<String, Object>();
    attributes.put("pid", pid);
    body.setAttributes(attributes);
    suchenLink = suchenLink + "?start=1&size=300";

    long start = System.nanoTime();
    Obj dokumentenliste = rb.doPut(suchenLink, Obj.class, body);
    long durationSearch = System.nanoTime() - start;
    updateStatistics(durationSearch, "PUT_FIND");

    LOGGER.debug("did search");

    return dokumentenliste;
  }


  private void getThumnbailsAndPDFs(RequestBroker rb, Obj dokumentenliste) {

    LOGGER.debug("start getting  individual thumbnails and pdfs");

    for (Obj searchItem : dokumentenliste.getObjList().values()) {
      String menuLink = searchItem.getLink("object").getHref();
      RunView view = new RunView(menuLink, rb, scenarioRunner);
      Future<Scenario> startedCall = scenarioRunner.run(view);
      addChildTask(startedCall);
    }
  }


  private String getSuchenLink(RequestBroker rb) {
    long start = System.nanoTime();
    Obj finder = rb.doGet(initialURI, Obj.class);
    long durationGetFinder = System.nanoTime() - start;

    updateStatistics(durationGetFinder, "GET_FINDER");

    finder = finder.getObjList().values().iterator().next();

    String suchenLink = finder.getLink("SUCHEN").getHref();
    LOGGER.debug("got suchenLink:" + suchenLink);
    return suchenLink;
  }

}
