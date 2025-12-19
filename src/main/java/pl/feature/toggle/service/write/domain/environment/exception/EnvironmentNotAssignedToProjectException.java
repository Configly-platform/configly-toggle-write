package pl.feature.toggle.service.write.domain.environment.exception;


import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

public class EnvironmentNotAssignedToProjectException extends RuntimeException {
    public EnvironmentNotAssignedToProjectException(EnvironmentId environmentId, ProjectId projectId) {
        super("Environment: [" + environmentId.uuid() + "] is not assigned to project: [" + projectId.uuid() + "]");
    }
}
