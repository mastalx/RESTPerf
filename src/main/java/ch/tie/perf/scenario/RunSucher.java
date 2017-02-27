package ch.tie.perf.scenario;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.Obj;


public class RunSucher extends AbstractScenario {

  private static final Logger LOGGER = LogManager.getLogger(RunSucher.class);

  private final ScenarioRunner scenarioRunner;
  private final String initialURI;
  private final String pid;
  private final RequestBroker requestBroker;

  private final boolean saveFile;

  public RunSucher(ScenarioRunner scenarioRunner,
      String initialURI,
      String pid,
      RequestBroker requestBroker,
      boolean saveFile) {
    this.scenarioRunner = scenarioRunner;
    this.initialURI = initialURI;
    this.pid = pid;
    this.requestBroker = requestBroker;
    this.saveFile = saveFile;
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
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("pid", pid);
    body.setAttributes(attributes);
    suchenLink = suchenLink + "?start=1&size=3000";

    Obj dokumentenliste = rb.doPut(suchenLink, Obj.class, body, "PUT_FIND");

    LOGGER.debug("did search");

    return dokumentenliste;
  }


  private void getThumnbailsAndPDFs(RequestBroker rb, Obj dokumentenliste) {

    LOGGER.debug("start getting  individual thumbnails and pdfs");

    dokumentenliste.getObjList()
        .values()
        .parallelStream()
        .map(searchItem -> searchItem.getLink("object").getHref())
        .map(menuLink -> new RunView(menuLink, rb, scenarioRunner, saveFile))
        .map(runView -> scenarioRunner.run(runView))
        .forEach(this::addChildTask);
  }


  private String getSuchenLink(RequestBroker rb) {

    Obj finder = rb.doGet(initialURI, Obj.class, "GET_FINDER");
    finder = finder.getObjList().values().iterator().next();

    String suchenLink = finder.getLink("SUCHEN").getHref();
    LOGGER.debug("got suchenLink:" + suchenLink);
    return suchenLink;
  }

}
