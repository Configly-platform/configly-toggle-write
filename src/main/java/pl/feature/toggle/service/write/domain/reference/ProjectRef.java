package pl.feature.toggle.service.write.domain.reference;

import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedProjectException;

import java.util.UUID;

public record ProjectRef(
        ProjectId projectId,
        ProjectStatus status,
        Revision lastRevision,
        boolean consistent
) {
    public static ProjectRef from(UUID projectId, String status, long revision) {
        return new ProjectRef(ProjectId.create(projectId), ProjectStatus.valueOf(status),
                Revision.from(revision), true);
    }

    public static ProjectRef from(ProjectId projectId, ProjectStatus status, Revision revision) {
        return new ProjectRef(projectId, status, revision, true);
    }

    public void assertIsActive() {
        if (!isActive()) {
            throw new CannotOperateOnArchivedProjectException(projectId);
        }
    }

    public ProjectRef apply(ProjectStatus newStatus, Revision newRevision) {
        return new ProjectRef(projectId, newStatus, newRevision, this.consistent);
    }

    public boolean isActive() {
        return ProjectStatus.ACTIVE.equals(status);
    }

    public boolean isArchived() {
        return ProjectStatus.ARCHIVED.equals(status);
    }
}
