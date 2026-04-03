package com.configly.toggle.write.application.projection.project;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.toggle.write.application.port.in.ProjectProjection;
import com.configly.toggle.write.application.port.in.ProjectRefConsistency;
import com.configly.toggle.write.application.port.out.ConfigurationClient;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.application.port.out.ProjectRefQueryRepository;

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
