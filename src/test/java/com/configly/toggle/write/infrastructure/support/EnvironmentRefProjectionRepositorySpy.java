package com.configly.toggle.write.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.toggle.write.StubSupport;
import com.configly.toggle.write.application.port.out.EnvironmentRefProjectionRepository;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

import java.util.ArrayList;
import java.util.List;

import static com.configly.toggle.write.StubSupport.forMethod;

public class EnvironmentRefProjectionRepositorySpy implements EnvironmentRefProjectionRepository {

    private final StubSupport<Boolean> markInconsistentIfNotMarked =
            forMethod("markInconsistentIfNotMarked(EnvironmentId)");


    public void markInconsistentIfNotMarkedReturns(boolean value) {
        markInconsistentIfNotMarked.willReturn(value);
    }

    public void markInconsistentIfNotMarkedThrows(RuntimeException ex) {
        markInconsistentIfNotMarked.willThrow(ex);
    }

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
