package de.leanovate.cucumber.examples;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber"}, tags = {"~@ignore"})
public class RunCukesTest {
}
