package pl.feature.toggle.service.write.application.projection.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectProjectionFacade {

    public static ProjectProjection projectProjection(ProjectRefRepository projectRefRepository,
                                                      ApplicationEventPublisher applicationEventPublisher) {
        return new ProjectProjectionHandler(projectRefRepository, applicationEventPublisher);
    }

    public static ProjectRefConsistency projectRefResolver(ConfigurationClient configurationClient, ProjectRefRepository projectRefRepository) {
        return new DefaultProjectRefConsistency(configurationClient, projectRefRepository);
    }

}
