package ch.tie.perf.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.tie.perf.model.Obj;
import ch.tie.perf.scenario.Statistics;


public class RequestBroker implements Closeable {

  private static final String ENCODING = "UTF-8";
  public static final Logger LOGGER = LogManager.getLogger(RequestBroker.class);

  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String CONTENT_TYPE_VALUE = "application/json";
  private static final String ACCEPT_HEADER = "Accept";
  private static final String IENGINE_USER = "IENGINE_USER";

  private final CloseableHttpClient restClient;
  private final String iengineUser;
  private final ObjectMapper objectMapper;
  private final Statistics statistics;

  /**
   * Constructor.
   */
  public RequestBroker(String iengineUser, String serviceUser, String servicePassword, Statistics statistics) {

    this.iengineUser = iengineUser;
    this.statistics = statistics;
    this.objectMapper = new ObjectMapper();
    this.restClient = createRestClient(serviceUser, servicePassword);
  }

  public <T> T doGet(final String uri, final Class<T> resultClass, String scenarioName) {
    final HttpGet getRequest = new HttpGet(uri);
    return doRequest(getRequest, uri, resultClass, scenarioName);
  }


  public <T> T doPut(final String uri, final Class<T> resultClass, final Obj body, String scenarioName) {
    final HttpPut putRequest = new HttpPut(uri);
    if (body != null) {
      setBody(putRequest, body);
    }
    return doRequest(putRequest, uri, resultClass, scenarioName);
  }

  private void setBody(final HttpEntityEnclosingRequestBase request, final Obj body) {
    try {
      request.setEntity(new StringEntity(objectMapper.writeValueAsString(body), ENCODING));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> T doRequest(final HttpRequestBase request,
      final String uri,
      final Class<T> resultClass,
      String scenarioName) {

    long start = System.nanoTime();
    // Do the rest service call
    request.addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE);
    request.addHeader(ACCEPT_HEADER, CONTENT_TYPE_VALUE);
    request.addHeader(IENGINE_USER, iengineUser);
    try (CloseableHttpResponse response = restClient.execute(request)) {

      // Check if an exception occurred on the server
      final StatusLine statusLine = response.getStatusLine();
      if (statusLine.getStatusCode() == 500) {
        final String responseContent = EntityUtils.toString(response.getEntity(), ENCODING);
        throw new RuntimeException(
            "error 500 occured request:" + request + System.lineSeparator() + " response: " + responseContent);
      }
      // Map the content to the requested result class


      InputStream content = response.getEntity().getContent();
      T retVal;
      if (byte[].class.equals(resultClass)) {
        @SuppressWarnings("unchecked")
        T byteArray = (T) IOUtils.toByteArray(content);
        retVal = byteArray;

      } else {
        retVal = objectMapper.readValue(content, resultClass);
      }
      long durationGetBytes = System.nanoTime() - start;
      statistics.updateStatistics(durationGetBytes, scenarioName);
      return retVal;
    } catch (UnsupportedOperationException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private CloseableHttpClient createRestClient(String username, String password) {


    LOGGER.info("Connect to health engine rest using user '" + username + "'");

    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(400);
    cm.setDefaultMaxPerRoute(40);
    return HttpClients.custom()
        .setDefaultCredentialsProvider(credentialsProvider)
        .setConnectionManager(cm)
        .setConnectionManagerShared(true)
        .build();

  }

  @Override
  public void close() throws IOException {
    if (restClient != null) {
      restClient.close();
    }

  }
}
