package pl.feature.toggle.service.write.application.projection.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectProjectionFacade {

    public static ProjectProjection projectProjection(ProjectRefProjectionRepository projectRefProjectionRepository,
                                                      ProjectRefQueryRepository projectRefQueryRepository,
                                                      ApplicationEventPublisher applicationEventPublisher,
                                                      RevisionProjectionApplier revisionProjectionApplier) {
        return new ProjectProjectionHandler(projectRefProjectionRepository,
                projectRefQueryRepository,
                revisionProjectionApplier,
                applicationEventPublisher);
    }

    public static ProjectRefConsistency projectRefResolver(ConfigurationClient configurationClient,
                                                           ProjectRefProjectionRepository projectRefProjectionRepository,
                                                           ProjectRefQueryRepository projectRefQueryRepository) {
        return new DefaultProjectRefConsistency(configurationClient,
                projectRefProjectionRepository,
                projectRefQueryRepository);
    }

}
