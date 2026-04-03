package com.configly.toggle.write.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.StubSupport;
import com.configly.toggle.write.application.port.out.EnvironmentRefQueryRepository;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

import java.util.Optional;

import static com.configly.toggle.write.StubSupport.forMethod;

public class EnvironmentRefQueryRepositoryStub implements EnvironmentRefQueryRepository {

    private final StubSupport<Optional<EnvironmentRef>> find =
            forMethod("find(ProjectId, EnvironmentId)");
    private final StubSupport<Optional<EnvironmentRef>> findConsistent =
            forMethod("findConsistent(ProjectId, EnvironmentId)");

    public void findReturns(EnvironmentRef value) {
        find.willReturn(Optional.ofNullable(value));
    }

    public void findThrows(RuntimeException ex) {
        find.willThrow(ex);
    }

    public void findConsistentReturns(EnvironmentRef value) {
        findConsistent.willReturn(Optional.ofNullable(value));
    }

    public void findConsistentThrows(RuntimeException ex) {
        findConsistent.willThrow(ex);
    }

    @Override
    public Optional<EnvironmentRef> find(ProjectId projectId, EnvironmentId environmentId) {
        return find.get();
    }

    @Override
    public Optional<EnvironmentRef> findConsistent(ProjectId projectId, EnvironmentId environmentId) {
        return findConsistent.get();
    }

    public void reset() {
        find.reset();
        findConsistent.reset();
    }
}
