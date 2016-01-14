package de.leanovate.cucumber.rest;

import org.apache.http.HttpResponse;
import org.assertj.core.api.Assertions;

public class RestAssertions extends Assertions {
    public static HttpResponseAssert assertThat(HttpResponse response) {
        return new HttpResponseAssert(response);
    }
}
