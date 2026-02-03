package pl.feature.toggle.service.write.domain.reference;

import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

public record EnvironmentRef(
        EnvironmentId environmentId,
        ProjectId projectId,
        EnvironmentStatus status
) {

    public static EnvironmentRef from(EnvironmentCreated event) {
        return new EnvironmentRef(
                EnvironmentId.create(event.environmentId()),
                ProjectId.create(event.projectId()),
                EnvironmentStatus.valueOf(event.status()));
    }

}
