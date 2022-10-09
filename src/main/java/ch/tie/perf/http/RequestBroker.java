package ch.tie.perf.http;

import ch.tie.perf.model.Obj;
import ch.tie.perf.scenario.FileHolder;
import ch.tie.perf.statistic.Statistics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;


public class RequestBroker implements Closeable {

    public static final Logger LOGGER = LogManager.getLogger(RequestBroker.class);

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String IENGINE_USER = "IENGINE_USER";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final CloseableHttpClient restClient;
    private final Optional<String> iengineUser;
    private final ObjectMapper objectMapper;
    private final Statistics statistics;

    private final AtomicLong corrIdCounter = new AtomicLong(0);

    /**
     * Constructor.
     */
    public RequestBroker(String iengineUser,
                         String serviceUser,
                         String servicePassword,
                         Statistics statistics) {
        this.iengineUser = Optional.of(iengineUser);
        this.statistics = statistics;
        this.objectMapper = new ObjectMapper();
        this.restClient = createRestClient(Optional.of(new UsernamePasswordCredentials(serviceUser, servicePassword)));
    }

    /**
     * Constructor.
     */
    public RequestBroker(Statistics statistics) {
        this.statistics = statistics;
        this.iengineUser = Optional.empty();
        this.objectMapper = new ObjectMapper();
        this.restClient = createRestClient();
    }

    public <T> T doGet(final String uri, final Class<T> resultClass, String scenarioName) {
        final HttpGet getRequest = new HttpGet(uri);
        return doRequest(getRequest, resultClass, scenarioName);
    }


    public <T> T doPut(final String uri, final Class<T> resultClass, final Obj body, String scenarioName) {
        final HttpPut putRequest = new HttpPut(uri);
        if (body != null) {
            setBody(putRequest, body);
        }
        return doRequest(putRequest, resultClass, scenarioName);
    }

    private void setBody(final HttpEntityEnclosingRequestBase request, final Obj body) {
        try {
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(body), StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T doRequest(final HttpRequestBase request,
                            final Class<T> resultClass,
                            String scenarioName) {

        long requestId = corrIdCounter.getAndIncrement();
        long start = System.nanoTime();
        // Do the rest service call
        request.addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE);
        request.addHeader(ACCEPT_HEADER, CONTENT_TYPE_VALUE);

        iengineUser.ifPresent(user -> request.addHeader(IENGINE_USER, user));

        LOGGER.debug("sending Request with Id: " + requestId);
        try (CloseableHttpResponse response = restClient.execute(request)) {

            // Check if an exception occurred on the server
            final StatusLine statusLine = response.getStatusLine();

            LOGGER.debug("got response with Id: " + requestId + ", Response Status: " + statusLine);
            int responseStatus = statusLine.getStatusCode();
            if (responseStatus != 200) {
                final String responseContent = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                throw new RuntimeException(
                        "response with Id: " + requestId + ", error " + responseStatus + " occured request:" + request
                                + System.lineSeparator() + " response: " + responseContent);
            }
            // Map the content to the requested result class
            try (InputStream content = response.getEntity().getContent()) {
                T retVal;
                if (FileHolder.class.equals(resultClass)) {
                    byte[] byteArray = IOUtils.toByteArray(content);
                    String fileName = getFileName(response, scenarioName);
                    retVal = (T) new FileHolder(fileName, byteArray);

                } else {
                    retVal = objectMapper.readValue(content, resultClass);
                }
                long end = System.nanoTime();
                statistics.updateStatistics(scenarioName, requestId, end - start);
                return retVal;
            }
        } catch (UnsupportedOperationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileName(CloseableHttpResponse response, String scenarioName) {
        return Arrays.stream(response.getHeaders("Content-Disposition"))
                .flatMap(header -> Arrays.stream(header.getElements()))
                .map(headerElement -> headerElement.getParameterByName("fileName"))
                .filter(Objects::nonNull)
                .map(NameValuePair::getValue)
                .map(fileName -> {
                    int indexOfFullStop = fileName.lastIndexOf('.');
                    String name = fileName.substring(0, indexOfFullStop);
                    String extension = fileName.substring(indexOfFullStop + 1);
                    return scenarioName + name + UUID.randomUUID() + "." + extension;
                })
                .findFirst()
                .orElse(scenarioName + UUID.randomUUID());
    }

    private CloseableHttpClient createRestClient(final Optional<UsernamePasswordCredentials> user) {
        LOGGER.info("Connect to endpoint using user");

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        user.ifPresent(u -> credentialsProvider.setCredentials(AuthScope.ANY, u));

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(400);
        cm.setDefaultMaxPerRoute(100);
        return HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .build();
    }

    private CloseableHttpClient createRestClient() {
        return createRestClient(Optional.empty());
    }

    @Override
    public void close() throws IOException {
        if (this.restClient != null) {
            this.restClient.close();
        }
    }
}
