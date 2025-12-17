package pl.feature.toggle.service.domain.project.exception;

import com.ftaas.domain.project.ProjectId;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId.uuid());
    }
}
