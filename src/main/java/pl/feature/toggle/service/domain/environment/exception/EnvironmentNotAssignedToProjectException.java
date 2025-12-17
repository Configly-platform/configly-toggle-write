package pl.feature.toggle.service.domain.environment.exception;

import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.project.ProjectId;

public class EnvironmentNotAssignedToProjectException extends RuntimeException {
    public EnvironmentNotAssignedToProjectException(EnvironmentId environmentId, ProjectId projectId) {
        super("Environment: [" + environmentId.uuid() + "] is not assigned to project: [" + projectId.uuid() + "]");
    }
}
