package com.configly.toggle.write.builder;

import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

public class FakeEnvironmentRefBuilder {
    private EnvironmentId environmentId;
    private ProjectId projectId;
    private EnvironmentStatus status;
    private Revision lastRevision;
    private boolean consistent;

    private FakeEnvironmentRefBuilder(){
        environmentId = EnvironmentId.create();
        projectId = ProjectId.create();
        status = EnvironmentStatus.ACTIVE;
        lastRevision = Revision.initialRevision();
        consistent = true;
    }

    public static FakeEnvironmentRefBuilder fakeEnvironmentRefBuilder() {
        return new FakeEnvironmentRefBuilder();
    }

    public FakeEnvironmentRefBuilder environmentId(EnvironmentId environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeEnvironmentRefBuilder projectId(ProjectId projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeEnvironmentRefBuilder status(EnvironmentStatus status) {
        this.status = status;
        return this;
    }

    public FakeEnvironmentRefBuilder lastRevision(Revision lastRevision) {
        this.lastRevision = lastRevision;
        return this;
    }

    public FakeEnvironmentRefBuilder consistent(boolean consistent) {
        this.consistent = consistent;
        return this;
    }

    public EnvironmentRef build() {
        return new EnvironmentRef(environmentId, projectId, status, lastRevision, consistent);
    }
}
