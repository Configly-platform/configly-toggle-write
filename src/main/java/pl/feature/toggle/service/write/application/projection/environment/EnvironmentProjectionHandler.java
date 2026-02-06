package pl.feature.toggle.service.write.application.projection.environment;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.projection.environment.event.RebuildEnvironmentRefRequested;
import pl.feature.toggle.service.write.application.projection.internal.RevisionProjectionApplier;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.EnvironmentStatus;

import java.util.UUID;

@AllArgsConstructor
class EnvironmentProjectionHandler implements EnvironmentProjection {

    private final EnvironmentRefRepository environmentRefRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RevisionProjectionApplier<EnvironmentRef> projectionRevisionApplier = new RevisionProjectionApplier<>();

    @Override
    public void handle(EnvironmentCreated event) {
        apply(event.revision(), event.projectId(), event.environmentId(), event.status());
    }

    @Override
    public void handle(EnvironmentStatusChanged event) {
        apply(event.revision(), event.projectId(), event.environmentId(), event.status());
    }

    private void apply(long revision, UUID projectIdRaw, UUID environmentIdRaw, String status) {
        var incomingRevision = Revision.from(revision);
        var projectId = ProjectId.create(projectIdRaw);
        var environmentId = EnvironmentId.create(environmentIdRaw);
        var environmentStatus = EnvironmentStatus.valueOf(status);
        var environmentRef = EnvironmentRef.from(projectId, environmentId, environmentStatus, incomingRevision);
        var internalEvent = new RebuildEnvironmentRefRequested(projectId, environmentId);
        projectionRevisionApplier.apply(
                incomingRevision,
                () -> environmentRefRepository.find(projectId, environmentId),
                () -> environmentRefRepository.insert(environmentRef),
                EnvironmentRef::lastRevision,
                current -> environmentRefRepository.update(current.apply(environmentStatus, incomingRevision)),
                () -> environmentRefRepository.markInconsistentIfNotMarked(environmentId),
                () -> eventPublisher.publishEvent(internalEvent)
        );
    }
}
