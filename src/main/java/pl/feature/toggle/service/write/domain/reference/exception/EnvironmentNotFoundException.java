package pl.feature.toggle.service.write.domain.reference.exception;


import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(ProjectId projectId, EnvironmentId environmentId) {
        super(String.format("Environment with id '%s' not found for project '%s'", environmentId.idAsString(), projectId.idAsString()));
    }
}
