package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.StubSupport;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.feature.toggle.service.write.StubSupport.forMethod;

public class EnvironmentRefRepositoryStubSpy implements EnvironmentRefRepository {
    private final StubSupport<Optional<EnvironmentRef>> find =
            forMethod("find(ProjectId, EnvironmentId)");
    private final StubSupport<Optional<EnvironmentRef>> findConsistent =
            forMethod("findConsistent(ProjectId, EnvironmentId)");
    private final StubSupport<Boolean> markInconsistentIfNotMarked =
            forMethod("markInconsistentIfNotMarked(EnvironmentId)");

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

    public void markInconsistentIfNotMarkedReturns(boolean value) {
        markInconsistentIfNotMarked.willReturn(value);
    }

    public void markInconsistentIfNotMarkedThrows(RuntimeException ex) {
        markInconsistentIfNotMarked.willThrow(ex);
    }

    // ---- spy (calls) ----

    private final List<EnvironmentRef> inserted = new ArrayList<>();
    private final List<EnvironmentRef> updated = new ArrayList<>();
    private final List<EnvironmentRef> upserted = new ArrayList<>();
    private final List<EnvironmentId> markedInconsistent = new ArrayList<>();

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
    public Optional<EnvironmentRef> find(ProjectId projectId, EnvironmentId environmentId) {
        return find.get();
    }

    @Override
    public Optional<EnvironmentRef> findConsistent(ProjectId projectId, EnvironmentId environmentId) {
        return findConsistent.get();
    }

    @Override
    public void insert(EnvironmentRef ref) {
        if (failOnAnyCall || noInserts) {
            throw new AssertionError("insert should not be called");
        }
        inserted.add(ref);
    }

    @Override
    public void update(EnvironmentRef ref) {
        if (failOnAnyCall || noUpdates) {
            throw new AssertionError("update should not be called");
        }
        updated.add(ref);
    }

    @Override
    public void upsert(EnvironmentRef ref) {
        if (failOnAnyCall || noUpserts) {
            throw new AssertionError("upsert should not be called");
        }
        upserted.add(ref);
    }

    @Override
    public boolean markInconsistentIfNotMarked(EnvironmentId environmentId) {
        if (failOnAnyCall || noConsistent) {
            throw new AssertionError("markInconsistentIfNotMarked should not be called");
        }
        markedInconsistent.add(environmentId);
        return markInconsistentIfNotMarked.get();
    }

    public EnvironmentRef lastInserted() {
        return inserted.isEmpty() ? null : inserted.getLast();
    }

    public EnvironmentRef lastUpdated() {
        return updated.isEmpty() ? null : updated.getLast();
    }

    public EnvironmentRef lastUpserted() {
        return upserted.isEmpty() ? null : upserted.getLast();
    }

    public int markInconsistentCalls() {
        return markedInconsistent.size();
    }

    public EnvironmentId lastMarkedInconsistent() {
        return markedInconsistent.isEmpty() ? null : markedInconsistent.getLast();
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

}
