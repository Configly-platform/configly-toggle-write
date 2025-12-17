package pl.feature.toggle.service.domain.project.exception;


import pl.feature.toggle.service.model.project.ProjectId;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId.uuid());
    }
}
