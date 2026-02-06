package pl.feature.toggle.service.write.application.projection.project;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.application.projection.internal.RevisionProjectionApplier;
import pl.feature.toggle.service.write.application.projection.project.event.RebuildProjectRefRequested;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.domain.reference.ProjectStatus;

import java.util.UUID;

@AllArgsConstructor
class ProjectProjectionHandler implements ProjectProjection {

    private final ProjectRefRepository projectRefRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RevisionProjectionApplier<ProjectRef> projectionRevisionApplier = new RevisionProjectionApplier<>();

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
        projectionRevisionApplier.apply(
                incomingRevision,
                () -> projectRefRepository.find(projectId),
                () -> projectRefRepository.insert(projectRef),
                ProjectRef::lastRevision,
                current -> projectRefRepository.update(current.apply(projectStatus, incomingRevision)),
                () -> projectRefRepository.markInconsistentIfNotMarked(projectId),
                () -> eventPublisher.publishEvent(internalEvent)
        );
    }


}

