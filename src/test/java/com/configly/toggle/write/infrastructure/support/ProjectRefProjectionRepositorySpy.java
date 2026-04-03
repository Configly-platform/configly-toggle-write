package com.configly.toggle.write.infrastructure.support;

import com.configly.model.project.ProjectId;
import com.configly.toggle.write.StubSupport;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.domain.reference.ProjectRef;

import java.util.ArrayList;
import java.util.List;

import static com.configly.toggle.write.StubSupport.forMethod;

public class ProjectRefProjectionRepositorySpy implements ProjectRefProjectionRepository {

    private final StubSupport<Boolean> markInconsistentIfNotMarked =
            forMethod("markInconsistentIfNotMarked(ProjectId)");

    public void markInconsistentIfNotMarkedReturns(boolean value) {
        markInconsistentIfNotMarked.willReturn(value);
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
