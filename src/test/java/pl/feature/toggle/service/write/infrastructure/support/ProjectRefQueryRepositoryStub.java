package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.StubSupport;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.Optional;

import static pl.feature.toggle.service.write.StubSupport.forMethod;

public class ProjectRefQueryRepositoryStub implements ProjectRefQueryRepository {

    private final StubSupport<Optional<ProjectRef>> find = forMethod("find(ProjectId)");
    private final StubSupport<Optional<ProjectRef>> findConsistent = forMethod("findConsistent(ProjectId)");

    public void findReturns(ProjectRef value) {
        find.willReturn(Optional.ofNullable(value));
    }

    public void findConsistentReturns(ProjectRef value) {
        findConsistent.willReturn(Optional.ofNullable(value));
    }

    public void findThrows(RuntimeException ex) {
        find.willThrow(ex);
    }

    public void findConsistentThrows(RuntimeException ex) {
        findConsistent.willThrow(ex);
    }

    public void reset() {
        find.reset();
        findConsistent.reset();
    }

    @Override
    public Optional<ProjectRef> find(ProjectId projectId) {
        return find.get();
    }

    @Override
    public Optional<ProjectRef> findConsistent(ProjectId projectId) {
        return findConsistent.get();
    }


}
