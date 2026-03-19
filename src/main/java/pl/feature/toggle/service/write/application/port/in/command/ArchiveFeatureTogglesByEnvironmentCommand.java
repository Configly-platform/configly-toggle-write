package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.web.actor.Actor;
import pl.feature.toggle.service.web.correlation.CorrelationId;

public record ArchiveFeatureTogglesByEnvironmentCommand(
        EnvironmentId environmentId,
        Actor actor,
        CorrelationId correlationId
) {
}
