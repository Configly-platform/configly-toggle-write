package com.configly.toggle.write.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.StubSupport;
import com.configly.toggle.write.application.port.out.ConfigurationClient;
import com.configly.toggle.write.domain.reference.EnvironmentRef;
import com.configly.toggle.write.domain.reference.ProjectRef;

import static com.configly.toggle.write.StubSupport.forMethod;

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
