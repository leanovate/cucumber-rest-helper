package de.leanovate.cucumber.rest;

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
}
