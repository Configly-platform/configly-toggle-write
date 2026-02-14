package pl.feature.toggle.service.write.builder;

import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

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
