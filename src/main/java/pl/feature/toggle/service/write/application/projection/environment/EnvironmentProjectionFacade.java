package pl.feature.toggle.service.write.application.projection.environment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentProjectionFacade {

    public static EnvironmentProjection environmentProjection(EnvironmentRefRepository environmentRefRepository,
                                                              ApplicationEventPublisher eventPublisher) {
        return new EnvironmentProjectionHandler(environmentRefRepository, eventPublisher);
    }

    public static EnvironmentRefConsistency environmentRefConsistency(ConfigurationClient configurationClient,
                                                                      EnvironmentRefRepository environmentRefRepository) {
        return new DefaultEnvironmentRefConsistency(configurationClient, environmentRefRepository);
    }
}
