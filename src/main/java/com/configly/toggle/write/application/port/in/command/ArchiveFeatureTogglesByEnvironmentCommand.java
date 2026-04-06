package com.configly.toggle.write.application.port.in.command;

import com.configly.model.environment.EnvironmentId;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;

public record ArchiveFeatureTogglesByEnvironmentCommand(
        EnvironmentId environmentId,
        Actor actor,
        CorrelationId correlationId
) {
}
