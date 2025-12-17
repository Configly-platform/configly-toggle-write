package pl.feature.toggle.service.application.port.in;

import com.ftaas.contracts.event.projects.EnvironmentCreated;
import com.ftaas.contracts.event.projects.ProjectCreated;

public interface ProjectEnvironmentProjectionUseCase {

    void handle(ProjectCreated event);

    void handle(EnvironmentCreated event);

}
