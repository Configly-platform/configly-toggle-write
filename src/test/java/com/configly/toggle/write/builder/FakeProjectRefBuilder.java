package com.configly.toggle.write.builder;

import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.domain.reference.ProjectRef;

public class FakeProjectRefBuilder {
    private ProjectId projectId;
    private ProjectStatus status;
    private Revision lastRevision;
    private boolean consistent;

    private FakeProjectRefBuilder(){
        projectId = ProjectId.create();
        status = ProjectStatus.ACTIVE;
        lastRevision = Revision.initialRevision();
        consistent = true;
    }

    public static FakeProjectRefBuilder fakeProjectRefBuilder() {
        return new FakeProjectRefBuilder();
    }


    public FakeProjectRefBuilder projectId(ProjectId projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeProjectRefBuilder status(ProjectStatus status) {
        this.status = status;
        return this;
    }

    public FakeProjectRefBuilder lastRevision(Revision lastRevision) {
        this.lastRevision = lastRevision;
        return this;
    }

    public FakeProjectRefBuilder consistent(boolean consistent) {
        this.consistent = consistent;
        return this;
    }

    public ProjectRef build() {
        return new ProjectRef(projectId, status, lastRevision, consistent);
    }
}
