package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.security.actor.Actor;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;

public record ArchiveFeatureTogglesByEnvironmentCommand(
        EnvironmentId environmentId,
        Actor actor,
        CorrelationId correlationId
) {
}
