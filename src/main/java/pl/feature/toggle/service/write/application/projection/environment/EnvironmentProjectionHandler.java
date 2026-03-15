package pl.feature.toggle.service.write.application.projection.environment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.event.processing.internal.RevisionApplierResult;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefQueryRepository;
import pl.feature.toggle.service.write.application.projection.environment.event.EnvironmentArchivedCascadeRequest;
import pl.feature.toggle.service.write.application.projection.environment.event.RebuildEnvironmentRefRequested;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

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

        var result = applyUpdateSnapshot(event.eventId(),incoming, projectId, environmentId, snapshot);

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
        var rebuildEvent = new RebuildEnvironmentRefRequested(projectId, environmentId);

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
            EventId eventId,
            Revision incoming,
            ProjectId projectId,
            EnvironmentId environmentId,
            EnvironmentRef snapshot
    ) {
        var rebuildEvent = new RebuildEnvironmentRefRequested(projectId, environmentId);

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
