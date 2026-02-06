package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;

public interface EnvironmentProjection {

    void handle(EnvironmentCreated event);

    void handle(EnvironmentStatusChanged event);
}
