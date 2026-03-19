package pl.feature.toggle.service.write.application.projection.environment.event;

import pl.feature.toggle.service.contracts.shared.Metadata;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.web.actor.Actor;
import pl.feature.toggle.service.web.correlation.CorrelationId;

public record EnvironmentArchivedCascadeRequest(
        EnvironmentId environmentId,
        Actor actor,
        CorrelationId correlationId
) {

    public static EnvironmentArchivedCascadeRequest create(EnvironmentId environmentId, Metadata metadata) {
        return new EnvironmentArchivedCascadeRequest(
                environmentId,
                Actor.create(metadata.actorId(), metadata.username()),
                CorrelationId.of(metadata.correlationId())
        );
    }
}
