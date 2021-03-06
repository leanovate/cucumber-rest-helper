package de.leanovate.cucumber.rest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpResponseAssert extends AbstractAssert<HttpResponseAssert, HttpResponse> {
    protected HttpResponseAssert(HttpResponse actual) {
        super(actual, HttpResponseAssert.class);
    }

    public HttpResponseAssert isNotFailure() {
        isNotNull();

        assertThat(actual.getStatusLine().getStatusCode()).isGreaterThanOrEqualTo(200).isLessThan(300);

        return myself;
    }

    public HttpResponseAssert isClientError() {
        isNotNull();

        assertThat(actual.getStatusLine().getStatusCode()).isGreaterThanOrEqualTo(400).isLessThan(500);

        return myself;
    }

    public HttpResponseAssert hasStatus(int status) {
        isNotNull();

        assertThat(actual.getStatusLine().getStatusCode()).isEqualTo(status);

        return myself;
    }

    public HttpResponseAssert isOk() {
        return hasStatus(200);
    }

    public HttpResponseAssert isCreated() {
        return hasStatus(201);
    }

    public HttpResponseAssert isNoContent() {
        return hasStatus(204);
    }

    public HttpResponseAssert isBadRequest() {
        return hasStatus(400);
    }

    public HttpResponseAssert isNotFound() {
        return hasStatus(404);
    }

    public HttpResponseAssert hasHeader(String name) {
        isNotNull();

        Header header = actual.getFirstHeader(name);

        assertThat(header).isNotNull();
        assertThat(header.getValue()).isNotEmpty();

        return myself;
    }
}
