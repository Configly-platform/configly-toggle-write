package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.StubSupport;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.feature.toggle.service.write.StubSupport.forMethod;

public class ProjectRefRepositoryStubSpy implements ProjectRefRepository {

    private final StubSupport<Optional<ProjectRef>> find = forMethod("find(ProjectId)");
    private final StubSupport<Optional<ProjectRef>> findConsistent = forMethod("findConsistent(ProjectId)");
    private final StubSupport<Boolean> markInconsistentIfNotMarked =
            forMethod("markInconsistentIfNotMarked(ProjectId)");

    public void findReturns(ProjectRef value) {
        find.willReturn(Optional.ofNullable(value));
    }

    public void findConsistentReturns(ProjectRef value) {
        findConsistent.willReturn(Optional.ofNullable(value));
    }

    public void markInconsistentIfNotMarkedReturns(boolean value) {
        markInconsistentIfNotMarked.willReturn(value);
    }

    public void findThrows(RuntimeException ex) {
        find.willThrow(ex);
    }

    public void findConsistentThrows(RuntimeException ex) {
        findConsistent.willThrow(ex);
    }

    public void markInconsistentIfNotMarkedThrows(RuntimeException ex) {
        markInconsistentIfNotMarked.willThrow(ex);
    }

    private final List<ProjectRef> inserted = new ArrayList<>();
    private final List<ProjectRef> updated = new ArrayList<>();
    private final List<ProjectRef> upserted = new ArrayList<>();
    private final List<ProjectId> markedInconsistent = new ArrayList<>();

    private boolean failOnAnyCall;
    private boolean noUpdates;
    private boolean noInserts;
    private boolean noUpserts;
    private boolean noConsistent;

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        find.reset();
        findConsistent.reset();
        markInconsistentIfNotMarked.reset();

        inserted.clear();
        updated.clear();
        upserted.clear();
        markedInconsistent.clear();

        failOnAnyCall = false;
        noUpdates = false;
        noInserts = false;
        noUpserts = false;
        noConsistent = false;
    }

    @Override
    public Optional<ProjectRef> find(ProjectId projectId) {
        return find.get();
    }

    @Override
    public Optional<ProjectRef> findConsistent(ProjectId projectId) {
        return findConsistent.get();
    }

    @Override
    public void insert(ProjectRef ref) {
        if (failOnAnyCall || noInserts) {
            throw new AssertionError("insert should not be called");
        }
        inserted.add(ref);
    }

    @Override
    public void update(ProjectRef ref) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(ref);
    }

    @Override
    public void upsert(ProjectRef ref) {
        if (failOnAnyCall || noUpserts) {
            throw new AssertionError("upsert should not be called");
        }
        upserted.add(ref);
    }

    @Override
    public boolean markInconsistentIfNotMarked(ProjectId projectId) {
        if (failOnAnyCall || noConsistent) {
            throw new AssertionError("markInconsistentIfNotMarked should not be called");
        }
        markedInconsistent.add(projectId);
        return markInconsistentIfNotMarked.get();
    }

    public ProjectRef lastUpserted() {
        return upserted.isEmpty() ? null : upserted.getLast();
    }

    public ProjectRef lastInserted() {
        return inserted.isEmpty() ? null : inserted.getLast();
    }

    public ProjectRef lastUpdated() {
        return updated.isEmpty() ? null : updated.getLast();
    }

    public void expectNoUpdates() {
        noUpdates = true;
    }

    public void expectNoUpserts() {
        noUpserts = true;
    }

    public void expectNoInserts() {
        noInserts = true;
    }

    public void expectNoMarkInconsistent() {
        noConsistent = true;
    }



    public ProjectId lastMarkedInconsistent() {
        return markedInconsistent.isEmpty() ? null : markedInconsistent.getLast();
    }
}
