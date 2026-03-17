package pl.feature.toggle.service.write.application.projection.project;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectUpdated;
import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.event.processing.internal.RevisionApplierResult;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.application.projection.project.event.RebuildProjectRefRequested;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@AllArgsConstructor
@Slf4j
class ProjectProjectionHandler implements ProjectProjection {

    private final ProjectRefProjectionRepository projectRefProjectionRepository;
    private final ProjectRefQueryRepository projectRefQueryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void handle(ProjectCreated event) {
        var result = applyCreate(event);
        if (result.wasApplied()) {
            log.info("Project-Projection created: projectId={},, revision={}",
                    event.projectId(), event.revision());
        }
    }

    @Transactional
    @Override
    public void handle(ProjectStatusChanged event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());
        var newStatus = ProjectStatus.valueOf(event.status());

        var snapshot = ProjectRef.from(projectId, newStatus, incoming);
        var result = applyUpdateSnapshot(event.correlationId(), event.eventId(), incoming, projectId, snapshot);
        if (result.wasApplied()) {
            log.info("Project-Projection status changed: projectId={}, newStatus={}, revision={}",
                    event.projectId(), newStatus, event.revision());
        }
    }

    private RevisionApplierResult applyCreate(ProjectCreated event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());
        var status = ProjectStatus.valueOf(event.status());

        var view = ProjectRef.from(projectId, status, incoming);
        var correlationId = CorrelationId.of(event.correlationId());
        var rebuildEvent = new RebuildProjectRefRequested(projectId, correlationId);

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectRef>forIncoming(incoming)
                        .eventId(event.eventId())
                        .findCurrentUsing(() -> projectRefQueryRepository.find(projectId))
                        .onMissing(() -> projectRefProjectionRepository.insert(view))
                        .extractCurrentRevisionUsing(ProjectRef::lastRevision)
                        .applyUpdateWhenApplicable(current ->
                                projectRefProjectionRepository.upsert(view)
                        )
                        .markInconsistentWhenGapDetectedIfNotMarked(
                                () -> projectRefProjectionRepository.markInconsistentIfNotMarked(projectId)
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
            ProjectRef snapshot
    ) {
        var rebuildEvent = new RebuildProjectRefRequested(projectId, CorrelationId.of(correlationId));

        return revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectRef>forIncoming(incoming)
                        .eventId(eventId)
                        .findCurrentUsing(() -> projectRefQueryRepository.find(projectId))
                        .onMissing(() -> projectRefProjectionRepository.upsert(snapshot))
                        .extractCurrentRevisionUsing(ProjectRef::lastRevision)
                        .applyUpdateWhenApplicable(current ->
                                projectRefProjectionRepository.upsert(snapshot)
                        )
                        .markInconsistentWhenGapDetectedIfNotMarked(
                                () -> projectRefProjectionRepository.markInconsistentIfNotMarked(projectId)
                        )
                        .publishRebuildWhenGapDetected(
                                () -> eventPublisher.publishEvent(rebuildEvent)
                        )
                        .build()
        );
    }
}

