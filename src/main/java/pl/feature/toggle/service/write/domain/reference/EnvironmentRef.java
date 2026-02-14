package pl.feature.toggle.service.write.domain.reference;

import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedEnvironmentException;

import java.util.UUID;

public record EnvironmentRef(
        EnvironmentId environmentId,
        ProjectId projectId,
        EnvironmentStatus status,
        Revision lastRevision,
        boolean consistent
) {

    public static EnvironmentRef from(UUID projectId, UUID environmentId, String status, long revision) {
        return new EnvironmentRef(
                EnvironmentId.create(environmentId),
                ProjectId.create(projectId),
                EnvironmentStatus.valueOf(status),
                Revision.from(revision),
                true);
    }

    public static EnvironmentRef from(ProjectId projectId, EnvironmentId environmentId, EnvironmentStatus status, Revision lastRevision) {
        return new EnvironmentRef(
                environmentId,
                projectId,
                status,
                lastRevision,
                true);
    }

    public void assertIsActive() {
        if (!isActive()) {
            throw new CannotOperateOnArchivedEnvironmentException(environmentId);
        }
    }

    public EnvironmentRef apply(EnvironmentStatus newStatus, Revision newRevision) {
        return new EnvironmentRef(environmentId, projectId, newStatus, newRevision, this.consistent);
    }

    public boolean isActive() {
        return EnvironmentStatus.ACTIVE == status;
    }

    public boolean isArchived() {
        return EnvironmentStatus.ARCHIVED == status;
    }

}
