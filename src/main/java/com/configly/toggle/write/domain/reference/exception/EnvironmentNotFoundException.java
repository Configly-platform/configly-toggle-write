package com.configly.toggle.write.domain.reference.exception;


import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(ProjectId projectId, EnvironmentId environmentId) {
        super(String.format("Environment with id '%s' not found for project '%s'", environmentId.idAsString(), projectId.idAsString()));
    }
}
