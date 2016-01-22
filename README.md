# Helper classes to test REST service using cucumber-jvm

This is only supposed to work with: [Cucumber JVM](https://github.com/cucumber/cucumber-jvm)

The main party trick is that all HTTP requests are recorded and added to the cucumber report so that it is obvious what is happening.

## Usage

Take a look in [/examples](/examples) or [Book DB example](https://github.com/leanovate/book-db-sample/tree/master/blackbox-tests/cucumber) for a full setup.

`pom.xml`

``` xml
<dependency>
    <groupId>de.leanovate.cucumber</groupId>
    <artifactId>rest-helper</artifactId>
    <version>0.9</version>
    <scope>test</scope>
</dependency>
```

Ensure that you include `classpath:de/leanovate/cucumber` in your glue path. Like this:

``` java
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber"}, tags = {"~@ignore"},
        glue = {"classpath:your/glue/package", "classpath:de/leanovate/cucumber"})
public class RunCukesTest {
}
```

Now you can inject the `TestHttpClient` into your stepdefs:

``` java
import de.leanovate.cucumber.rest.TestHttpClient;
import org.apache.http.client.fluent.Request;

import static de.leanovate.cucumber.rest.RestAssertions.assertThat;

public class MyStepdefs {
    private final TestHttpClient client;
    
    public MyStepdefs(TestHttpClient client) {
    	  this.client = client;
    }
    
    @When("^Get the thing$")
    public void get_the_thing() throws Throwable {
        Request request = Request.Get("http://localhost/thing");
    		
        HttpResponse response = client.execute(request);
        
        assertThat(response).isOk();
    }
}
```

