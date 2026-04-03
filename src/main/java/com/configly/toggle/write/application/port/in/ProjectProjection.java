package com.configly.toggle.write.application.port.in;


import com.configly.contracts.event.project.ProjectCreated;
import com.configly.contracts.event.project.ProjectStatusChanged;

public interface ProjectProjection {

    void handle(ProjectCreated event);

    void handle(ProjectStatusChanged event);
}
