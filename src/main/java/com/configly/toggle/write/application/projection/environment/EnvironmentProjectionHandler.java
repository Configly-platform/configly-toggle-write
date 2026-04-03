package com.configly.toggle.write.application.projection.environment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import com.configly.contracts.event.environment.EnvironmentCreated;
import com.configly.contracts.event.environment.EnvironmentStatusChanged;
import com.configly.contracts.shared.EventId;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.event.processing.api.RevisionProjectionPlan;
import com.configly.event.processing.internal.RevisionApplierResult;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;
import com.configly.web.correlation.CorrelationId;
import com.configly.toggle.write.application.port.in.EnvironmentProjection;
import com.configly.toggle.write.application.port.out.EnvironmentRefProjectionRepository;
import com.configly.toggle.write.application.port.out.EnvironmentRefQueryRepository;
import com.configly.toggle.write.application.projection.environment.event.EnvironmentArchivedCascadeRequest;
import com.configly.toggle.write.application.projection.environment.event.RebuildEnvironmentRefRequested;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

@AllArgsConstructor
@Slf4j
class EnvironmentProjectionHandler implements EnvironmentProjection {

    private final EnvironmentRefProjectionRepository environmentRefProjectionRepository;
    private final EnvironmentRefQueryRepository environmentRefQueryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RevisionProjectionApplier revisionProjectionApplier;

    @Override
    @Transactional
    public void handle(EnvironmentCreated event) {
        var result = applyCreate(event);
        if (result.wasApplied()) {
            log.info("Environment-Projection created: environmentId={}, projectId={}, revision={}", event.environmentId(),
                    event.projectId(), event.revision());
        }
    }

    @Override
    @Transactional
    public void handle(EnvironmentStatusChanged event) {
        var environmentId = EnvironmentId.create(event.environmentId());
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());
        var newStatus = EnvironmentStatus.valueOf(event.status());

        var snapshot = EnvironmentRef.from(projectId, environmentId, newStatus, incoming);

        var result = applyUpdateSnapshot(event.correlationId(), event.eventId(), incoming, projectId, environmentId, snapshot);

        if (result.wasApplied() && newStatus.isArchived()) {
            eventPublisher.publishEvent(EnvironmentArchivedCascadeRequest.create(environmentId, event.metadata()));
            log.info("Environment-Projection status changed: environmentId={}, projectId={}, newStatus={}, revision={}",
                    event.environmentId(), event.projectId(), event.status(), event.revision());
        }
    }

    private RevisionApplierResult applyCreate(EnvironmentCreated event) {
        var projectId = ProjectId.create(event.projectId());
        var environmentId = EnvironmentId.create(event.environmentId());
        var incomingRevision = Revision.from(event.revision());
        var status = EnvironmentStatus.valueOf(event.status());
        var view = EnvironmentRef.from(projectId, environmentId, status, incomingRevision);
        var correlationId = CorrelationId.of(event.correlationId());
        var rebuildEvent = new RebuildEnvironmentRefRequested(projectId, environmentId, correlationId);

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<EnvironmentRef>forIncoming(incomingRevision)
                        .eventId(event.eventId())
                        .findCurrentUsing(() -> environmentRefQueryRepository.find(projectId, environmentId))
                        .onMissing(() -> environmentRefProjectionRepository.insert(view))
                        .extractCurrentRevisionUsing(EnvironmentRef::lastRevision)
                        .applyUpdateWhenApplicable(current ->
                                environmentRefProjectionRepository.upsert(view)
                        )
                        .markInconsistentWhenGapDetectedIfNotMarked(
                                () -> environmentRefProjectionRepository.markInconsistentIfNotMarked(environmentId)
                        )
                        .publishRebuildWhenGapDetected(
                                () -> eventPublisher.publishEvent(rebuildEvent)
                        )
                        .build()
        );
    }

    private RevisionApplierResult applyUpdateSnapshot(
            String correlationId,
            EventId eventId,
            Revision incoming,
            ProjectId projectId,
            EnvironmentId environmentId,
            EnvironmentRef snapshot
    ) {
        var rebuildEvent = new RebuildEnvironmentRefRequested(projectId, environmentId, CorrelationId.of(correlationId));

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<EnvironmentRef>forIncoming(incoming)
                        .eventId(eventId)
                        .findCurrentUsing(() -> environmentRefQueryRepository.find(projectId, environmentId))
                        .onMissing(() -> environmentRefProjectionRepository.upsert(snapshot))
                        .extractCurrentRevisionUsing(EnvironmentRef::lastRevision)
                        .applyUpdateWhenApplicable(current ->
                                environmentRefProjectionRepository.upsert(snapshot)
                        )
                        .markInconsistentWhenGapDetectedIfNotMarked(
                                () -> environmentRefProjectionRepository.markInconsistentIfNotMarked(environmentId)
                        )
                        .publishRebuildWhenGapDetected(
                                () -> eventPublisher.publishEvent(rebuildEvent)
                        )
                        .build()
        );
    }
}
