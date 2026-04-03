package com.configly.toggle.write.application.port.in;

import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.ProjectRef;

public interface ProjectRefConsistency {
    ProjectRef getTrusted(ProjectId projectId);

    void rebuild(ProjectId projectId);
}
