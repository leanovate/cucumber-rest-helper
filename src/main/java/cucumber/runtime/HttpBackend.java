package cucumber.runtime;

import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.snippets.FunctionNameGenerator;
import gherkin.formatter.model.Step;

import java.util.List;

public class HttpBackend implements Backend{
    public HttpBackend(ResourceLoader resourceLoader) {
    }

    @Override
    public void loadGlue(Glue glue, List<String> gluePaths) {
        System.out.println(">>> " + gluePaths);
    }

    @Override
    public void setUnreportedStepExecutor(UnreportedStepExecutor executor) {

    }

    @Override
    public void buildWorld() {

    }

    @Override
    public void disposeWorld() {

    }

    @Override
    public String getSnippet(Step step, FunctionNameGenerator functionNameGenerator) {
        return null;
    }
}
