package com.configly.toggle.write.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.StubSupport;
import com.configly.toggle.write.application.port.out.ConfigurationClient;
import com.configly.toggle.write.domain.reference.EnvironmentRef;
import com.configly.toggle.write.domain.reference.ProjectRef;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationClientSpy implements ConfigurationClient {
    private final StubSupport<EnvironmentRef> fetchEnvironment =
            StubSupport.forMethod("fetchEnvironment(ProjectId, EnvironmentId)");

    private final List<ProjectId> calledProjectIds = new ArrayList<>();
    private final List<EnvironmentId> calledEnvironmentIds = new ArrayList<>();

    private boolean failOnAnyCall;

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        fetchEnvironment.reset();
        calledProjectIds.clear();
        calledEnvironmentIds.clear();
        failOnAnyCall = false;
    }

    public void fetchEnvironmentReturns(EnvironmentRef value) {
        fetchEnvironment.willReturn(value);
    }

    public int fetchEnvironmentCalls() {
        return calledProjectIds.size();
    }

    @Override
    public EnvironmentRef fetchEnvironment(ProjectId projectId, EnvironmentId environmentId) {
        if (failOnAnyCall) {
            throw new AssertionError("fetchEnvironment should not be called");
        }
        calledProjectIds.add(projectId);
        calledEnvironmentIds.add(environmentId);
        return fetchEnvironment.get();
    }

    @Override
    public ProjectRef fetchProject(ProjectId projectId) {
        throw new UnsupportedOperationException("Not needed in this test");
    }
}
