package pl.feature.toggle.service.write.application.port.in;


import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;

public interface ProjectProjection {

    void handle(ProjectCreated event);

    void handle(ProjectStatusChanged event);
}
