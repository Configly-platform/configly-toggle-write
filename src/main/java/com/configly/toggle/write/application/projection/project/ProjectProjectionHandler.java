package com.configly.toggle.write.application.projection.project;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import com.configly.contracts.event.project.ProjectCreated;
import com.configly.contracts.event.project.ProjectStatusChanged;
import com.configly.contracts.shared.EventId;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.event.processing.api.RevisionProjectionPlan;
import com.configly.event.processing.internal.RevisionApplierResult;
import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.toggle.write.application.port.in.ProjectProjection;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.application.port.out.ProjectRefQueryRepository;
import com.configly.toggle.write.application.projection.project.event.RebuildProjectRefRequested;
import com.configly.toggle.write.domain.reference.ProjectRef;

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

