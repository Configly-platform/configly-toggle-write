package com.configly.toggle.write.domain.reference.exception;


import com.configly.model.project.ProjectId;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(ProjectId projectId) {
        super("Project not found: " + projectId.uuid());
    }
}
