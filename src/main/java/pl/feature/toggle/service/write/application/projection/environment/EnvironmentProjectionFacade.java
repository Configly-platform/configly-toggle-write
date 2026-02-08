package pl.feature.toggle.service.write.application.projection.environment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefQueryRepository;

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
