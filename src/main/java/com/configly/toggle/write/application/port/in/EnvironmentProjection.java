package com.configly.toggle.write.application.port.in;

import com.configly.contracts.event.environment.EnvironmentCreated;
import com.configly.contracts.event.environment.EnvironmentStatusChanged;

public interface EnvironmentProjection {

    void handle(EnvironmentCreated event);

    void handle(EnvironmentStatusChanged event);
}
