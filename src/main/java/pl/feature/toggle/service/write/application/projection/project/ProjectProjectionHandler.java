package pl.feature.toggle.service.write.application.projection.project;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionPlan;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.application.projection.project.event.RebuildProjectRefRequested;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@AllArgsConstructor
class ProjectProjectionHandler implements ProjectProjection {

    private final ProjectRefProjectionRepository projectRefProjectionRepository;
    private final ProjectRefQueryRepository projectRefQueryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void handle(ProjectCreated event) {
        applyCreate(event);
    }

    @Transactional
    @Override
    public void handle(ProjectStatusChanged event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());
        var newStatus = ProjectStatus.valueOf(event.status());

        var snapshot = ProjectRef.from(projectId, newStatus, incoming);
        applyUpdateSnapshot(incoming, projectId, snapshot);
    }

    private void applyCreate(ProjectCreated event) {
        var projectId = ProjectId.create(event.projectId());
        var incoming = Revision.from(event.revision());
        var status = ProjectStatus.valueOf(event.status());

        var view = ProjectRef.from(projectId, status, incoming);
        var rebuildEvent = new RebuildProjectRefRequested(projectId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectRef>forIncoming(incoming)
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

    private void applyUpdateSnapshot(
            Revision incoming,
            ProjectId projectId,
            ProjectRef snapshot
    ) {
        var rebuildEvent = new RebuildProjectRefRequested(projectId);

        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectRef>forIncoming(incoming)
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

