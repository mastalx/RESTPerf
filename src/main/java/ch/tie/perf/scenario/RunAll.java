package ch.tie.perf.scenario;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.Link;

import ch.tie.perf.http.RequestBroker;
import ch.tie.perf.model.Obj;

public class RunAll {

  private static final Logger LOGGER = LogManager.getLogger(RunAll.class);
  private static final Path BINARIES_PATH = Paths.get("binaries");

  private final Executor executor;
  private final String initialURI;
  private final String pid;
  private final RequestBroker requestBroker;
  private final boolean saveFile;

  public RunAll(Executor executor, String initialURI, String pid, RequestBroker requestBroker, boolean saveFile) {
    this.executor = executor;
    this.initialURI = initialURI;
    this.pid = pid;
    this.requestBroker = requestBroker;
    this.saveFile = saveFile;
  }

  public Stream<CompletableFuture<Void>> run() {
    return supplyAsync(this::getSuchenLink, executor).thenApplyAsync(this::doSearch, executor)
        .thenApplyAsync(dokumentenliste -> dokumentenliste.getObjList()
            .values()
            .parallelStream()
            .flatMap(searchItem -> runView(searchItem.getLink("object").getHref())), executor)
        .join();
  }

  private Obj doSearch(String suchenLink) {
    Obj body = new Obj();
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("pid", pid);
    body.setAttributes(attributes);
    suchenLink = suchenLink + "?start=1&size=3000";
    Obj dokumentenliste = requestBroker.doPut(suchenLink, Obj.class, body, "PUT_FIND");
    LOGGER.debug("did search");
    return dokumentenliste;
  }


  private String getSuchenLink() {
    Obj finder = requestBroker.doGet(initialURI, Obj.class, "GET_FINDER");
    finder = finder.getObjList().values().iterator().next();

    String suchenLink = finder.getLink("SUCHEN").getHref();
    LOGGER.debug("got suchenLink: {}", suchenLink);
    return suchenLink;
  }


  private Stream<CompletableFuture<Void>> runView(final String menuLink) {
    return supplyAsync(() -> getLinks(menuLink), executor).thenApplyAsync(links -> {
      CompletableFuture<Void> view = runAsync(() -> getBytes(links.viewLink, "GET_PDF"), executor);
      CompletableFuture<Void> stream = runAsync(() -> getBytes(links.streamLink, "GET_PDF_STREAMED"), executor);
      CompletableFuture<Void> tNail = runAsync(() -> getBytes(links.thumbnailLink, "GET_THUMBNAIL"), executor);
      return Stream.of(view, stream, tNail);
    }, executor).join();
  }


  private Links getLinks(String menuLink) {
    final Obj menu = requestBroker.doGet(menuLink, Obj.class, "GET_DOCUMENT_MENU");
    Link link;
    if (menu == null || (link = menu.getLink("VIEW")) == null) {
      return Links.EMTPY_LINKS;
    }
    String viewLink = link.getHref();
    String streamLink = menu.getLink("STREAM").getHref();
    String thumbnailLink = viewLink + "?imageType=THUMBNAIL_M";
    return new Links(viewLink, streamLink, thumbnailLink);
  }


  private void getBytes(String viewLink, String category) {
    try {
      LOGGER.debug("start getting bytes on category: {} with link: {}", category, viewLink);
      FileHolder file = requestBroker.doGet(viewLink, FileHolder.class, category);

      if (saveFile) {
        Path temppdf = Paths.get(BINARIES_PATH.toString(), file.getFileName());
        Files.write(temppdf, file.getBytes(), StandardOpenOption.CREATE);
      }

      LOGGER.debug("finished getting bytes on category: {} with link: {}", category, viewLink);

    } catch (Exception e) {
      LOGGER.error("error in getting bytes", e);
    }
  }

  private static class Links {

    private static final Links EMTPY_LINKS = new Links("", "", "");

    private final String viewLink;
    private final String streamLink;
    private final String thumbnailLink;

    Links(String viewLink, String streamLink, String thumbnailLink) {
      this.viewLink = viewLink;
      this.streamLink = streamLink;
      this.thumbnailLink = thumbnailLink;
    }
  }
}
