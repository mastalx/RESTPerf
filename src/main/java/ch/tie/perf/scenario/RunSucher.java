package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ch.tie.perf.ScenarioRunner;
import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.Obj;


public class RunSucher extends StatisticsScenario {

  private final ScenarioRunner scenarioRunner;
  private final String iengineUser;
  private final String serviceUser;
  private final String servicePassword;
  private final String initialURI;
  private final String pid;

  public RunSucher(ScenarioRunner scenarioRunner,
      String iengineUser,
      String serviceUser,
      String servicePassword,
      String initialURI,
      String pid) {
    this.scenarioRunner = scenarioRunner;
    this.iengineUser = iengineUser;
    this.serviceUser = serviceUser;
    this.servicePassword = servicePassword;
    this.initialURI = initialURI;
    this.pid = pid;
  }


  @Override
  public RunSucher call() throws Exception {
    try (RequestBroker rb = new RequestBroker(iengineUser, serviceUser, servicePassword)) {

      String suchenLink = getSuchenLink(rb);
      Obj dokumentenliste = doSearch(rb, suchenLink);

      getThumnbailsAndPDFs(rb, dokumentenliste);

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

    return dokumentenliste;
  }


  private void getThumnbailsAndPDFs(RequestBroker rb, Obj dokumentenliste) {
    List<Future<Scenario>> startedCalls = new ArrayList<Future<Scenario>>();
    for (Obj searchItem : dokumentenliste.getObjList().values()) {
      String menuLink = searchItem.getLink("object").getHref();
      RunView view = new RunView(menuLink, rb);
      Future<Scenario> startedCall = scenarioRunner.run(view);
      startedCalls.add(startedCall);
    }

    for (Future<Scenario> future : startedCalls) {
      try {
        RunView view = (RunView) future.get();
        mergeStatistics(view.getStatistics());
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
  }


  private String getSuchenLink(RequestBroker rb) {
    long start = System.nanoTime();
    Obj finder = rb.doGet(initialURI, Obj.class);
    long durationGetFinder = System.nanoTime() - start;

    updateStatistics(durationGetFinder, "GET_FINDER");

    finder = finder.getObjList().values().iterator().next();

    String suchenLink = finder.getLink("SUCHEN").getHref();
    return suchenLink;
  }

}
