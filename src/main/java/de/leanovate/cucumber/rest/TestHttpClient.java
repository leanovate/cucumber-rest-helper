package de.leanovate.cucumber.rest;

import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.Optional;

import static de.leanovate.cucumber.rest.RestAssertions.assertThat;

public class TestHttpClient {
    public final Executor http;

    public final Executor noReportHttp;

    private Optional<Scenario> scenario = Optional.empty();

    public TestHttpClient() {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((chain, authType) -> true)
                    .build();
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setMaxConnTotal(100)
                    .setMaxConnPerRoute(100)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .setSSLContext(sslContext)
                    .addInterceptorLast(new RequestReporter())
                    .addInterceptorLast(new ResponseReporter())
                    .disableRedirectHandling()
                    .build();
            CloseableHttpClient noReportHttpClient = HttpClientBuilder.create()
                    .setMaxConnTotal(100)
                    .setMaxConnPerRoute(100)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .setSSLContext(sslContext)
                    .disableRedirectHandling()
                    .build();

            this.http = Executor.newInstance(httpClient);
            this.noReportHttp = Executor.newInstance(noReportHttpClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setScenario(Scenario scenario) {
        this.scenario = Optional.of(scenario);
    }

    public HttpResponse execute(Request request) {
        return execute(request,(response) -> response, true);
    }

    public <T> T execute(Request request, ResponseHandler<T> responseHandler, boolean report) {
        try {
            if (report) {
                return http.execute(request).handleResponse(responseHandler);
            } else {
                return noReportHttp.execute(request).handleResponse(responseHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T executeJson(Request request, Class<T> expected) {
        return executeJson(request, expected, true);
    }

    public <T> T executeJson(Request request, Class<T> expected, boolean report) {
        return execute(request, (response) -> {
            assertThat(response).isNotFailure();
            T result = JsonHelper.entityToValue(response.getEntity(), expected);

            Header etagHeader = response.getFirstHeader(HttpHeaders.ETAG);
            if (etagHeader != null && (result instanceof WithEtag)) {
                ((WithEtag) result).updateEtag(etagHeader.getValue());
            }

            return result;
        }, report);
    }

    public JsonNode executeJsonTree(Request request) {
        return executeJsonTree(request, true);
    }

    public JsonNode executeJsonTree(Request request, boolean report) {
        return execute(request, (response) -> {
            assertThat(response).isNotFailure();
            return JsonHelper.entityToTree(response.getEntity());
        }, report);
    }

    public Document executeHtml(Request request) {
        return executeHtml(request, true);
    }

    public Document executeHtml(Request request, boolean report) {
        return execute(request, (response) -> {
            assertThat(response).isNotFailure();
            return Jsoup.parse(response.getEntity().getContent(), "UTF-8", "");
        }, report);
    }

    class RequestReporter implements HttpRequestInterceptor {

        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            scenario.ifPresent((scenario) -> {
                scenario.write(Utils.formatRequest(request));
            });
        }
    }

    class ResponseReporter implements HttpResponseInterceptor {

        @Override
        public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
            scenario.ifPresent((scenario) -> {
                scenario.write(Utils.formatResponse(response));
            });
        }
    }
}
