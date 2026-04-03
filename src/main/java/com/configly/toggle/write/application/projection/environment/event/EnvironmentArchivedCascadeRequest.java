package com.configly.toggle.write.application.projection.environment.event;

import com.configly.contracts.shared.Metadata;
import com.configly.model.environment.EnvironmentId;
import com.configly.web.actor.Actor;
import com.configly.web.correlation.CorrelationId;

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
