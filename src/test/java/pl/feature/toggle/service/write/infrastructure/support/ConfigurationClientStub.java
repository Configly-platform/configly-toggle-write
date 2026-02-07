package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.StubSupport;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import static pl.feature.toggle.service.write.StubSupport.forMethod;

public class ConfigurationClientStub implements ConfigurationClient {

    private final StubSupport<ProjectRef> fetchProject =
            forMethod("fetchProject(ProjectId)");
    private final StubSupport<EnvironmentRef> fetchEnvironment =
            forMethod("fetchEnvironment(ProjectId, EnvironmentId)");

    private boolean failOnAnyCall = false;

    public void fetchProjectReturns(ProjectRef ref) {
        fetchProject.willReturn(ref);
    }

    public void fetchProjectThrows(RuntimeException ex) {
        fetchProject.willThrow(ex);
    }

    public void fetchEnvironmentReturns(EnvironmentRef ref) {
        fetchEnvironment.willReturn(ref);
    }

    public void fetchEnvironmentThrows(RuntimeException ex) {
        fetchEnvironment.willThrow(ex);
    }


    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        fetchProject.reset();
        fetchEnvironment.reset();
    }

    @Override
    public ProjectRef fetchProject(ProjectId projectId) {
        if (failOnAnyCall) {
            throw new AssertionError("fetch project should not be called");
        }
        return fetchProject.get();
    }

    @Override
    public EnvironmentRef fetchEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        if (failOnAnyCall) {
            throw new AssertionError("fetch envrionement should not be called");
        }
        return fetchEnvironment.get();
    }
}
