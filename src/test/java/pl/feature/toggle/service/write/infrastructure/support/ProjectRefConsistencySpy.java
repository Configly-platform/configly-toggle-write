package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.StubSupport;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.ArrayList;
import java.util.List;

public class ProjectRefConsistencySpy implements ProjectRefConsistency {

    private final StubSupport<ProjectRef> getTrusted =
            StubSupport.forMethod("getTrusted(ProjectId)");

    private final List<ProjectId> trustedProjectIds = new ArrayList<>();
    private final List<ProjectId> rebuiltProjectIds = new ArrayList<>();

    private boolean failOnAnyCall = false;

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        getTrusted.reset();
        trustedProjectIds.clear();
        rebuiltProjectIds.clear();
    }

    public void getTrustedReturns(ProjectRef value) {
        getTrusted.willReturn(value);
    }

    public void getTrustedThrows(RuntimeException ex) {
        getTrusted.willThrow(ex);
    }

    @Override
    public ProjectRef getTrusted(ProjectId projectId) {
        if (failOnAnyCall) {
            throw new AssertionError("get trusted should not be called");
        }
        trustedProjectIds.add(projectId);
        return getTrusted.get();
    }

    @Override
    public void rebuild(ProjectId projectId) {
        if (failOnAnyCall) {
            throw new AssertionError("rebuild should not be called");
        }
        rebuiltProjectIds.add(projectId);
    }

    public int getTrustedCalls() {
        return trustedProjectIds.size();
    }

    public int rebuildCalls() {
        return rebuiltProjectIds.size();
    }

    public ProjectId lastGetTrustedProjectId() {
        return trustedProjectIds.isEmpty() ? null : trustedProjectIds.getLast();
    }

    public ProjectId lastRebuildProjectId() {
        return rebuiltProjectIds.isEmpty() ? null : rebuiltProjectIds.getLast();
    }
}
