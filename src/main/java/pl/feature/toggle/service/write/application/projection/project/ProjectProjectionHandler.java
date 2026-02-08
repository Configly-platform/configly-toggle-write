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
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.application.projection.project.event.RebuildProjectRefRequested;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.domain.reference.ProjectStatus;

import java.util.UUID;

@AllArgsConstructor
class ProjectProjectionHandler implements ProjectProjection {

    private final ProjectRefProjectionRepository projectRefProjectionRepository;
    private final ProjectRefQueryRepository projectRefQueryRepository;
    private final RevisionProjectionApplier revisionProjectionApplier;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void handle(ProjectCreated event) {
        apply(event.revision(), event.projectId(), event.status());
    }

    @Transactional
    @Override
    public void handle(ProjectStatusChanged event) {
        apply(event.revision(), event.projectId(), event.status());
    }

    private void apply(long revision, UUID projectIdRaw, String status) {
        var incomingRevision = Revision.from(revision);
        var projectId = ProjectId.create(projectIdRaw);
        var projectStatus = ProjectStatus.valueOf(status);
        var projectRef = ProjectRef.from(projectId, projectStatus, incomingRevision);
        var internalEvent = new RebuildProjectRefRequested(projectId);
        revisionProjectionApplier.apply(
                RevisionProjectionPlan.<ProjectRef>forIncoming(incomingRevision)
                        .findCurrentUsing(() -> projectRefQueryRepository.find(projectId))
                        .insertWhenMissing(() -> projectRefProjectionRepository.insert(projectRef))
                        .extractCurrentRevisionUsing(ProjectRef::lastRevision)
                        .applyUpdateWhenApplicable(current ->
                                projectRefProjectionRepository.update(current.apply(projectStatus, incomingRevision))
                        )
                        .markInconsistentWhenGapDetectedIfNotMarked(
                                () -> projectRefProjectionRepository.markInconsistentIfNotMarked(projectId)
                        )
                        .publishRebuildWhenGapDetected(
                                () -> eventPublisher.publishEvent(internalEvent)
                        )
                        .build()
        );
    }


}

