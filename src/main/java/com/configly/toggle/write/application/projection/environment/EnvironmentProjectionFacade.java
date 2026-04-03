package com.configly.toggle.write.application.projection.environment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.configly.event.processing.api.RevisionProjectionApplier;
import com.configly.toggle.write.application.port.in.EnvironmentProjection;
import com.configly.toggle.write.application.port.in.EnvironmentRefConsistency;
import com.configly.toggle.write.application.port.out.ConfigurationClient;
import com.configly.toggle.write.application.port.out.EnvironmentRefProjectionRepository;
import com.configly.toggle.write.application.port.out.EnvironmentRefQueryRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentProjectionFacade {

    public static EnvironmentProjection environmentProjection(EnvironmentRefProjectionRepository environmentRefProjectionRepository,
                                                              EnvironmentRefQueryRepository environmentRefQueryRepository,
                                                              RevisionProjectionApplier revisionProjectionApplier,
                                                              ApplicationEventPublisher eventPublisher) {
        return new EnvironmentProjectionHandler(environmentRefProjectionRepository,
                environmentRefQueryRepository,
                eventPublisher,
                revisionProjectionApplier);
    }

    public static EnvironmentRefConsistency environmentRefConsistency(ConfigurationClient configurationClient,
                                                                      EnvironmentRefProjectionRepository environmentRefProjectionRepository,
                                                                      EnvironmentRefQueryRepository environmentRefQueryRepository) {
        return new DefaultEnvironmentRefConsistency(configurationClient,
                environmentRefProjectionRepository,
                environmentRefQueryRepository);
    }
}
