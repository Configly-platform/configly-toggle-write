package pl.feature.toggle.service.write.application.policy;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;

@AllArgsConstructor
public class FeatureTogglePolicyFacade {

    private final UniqueFeatureToggleNameInEnvironmentPolicy uniqueFeatureToggleNameInEnvironmentPolicy;

    public static FeatureTogglePolicyFacade create(
            FeatureToggleQueryRepository featureToggleQueryRepository
    ) {
        var uniqueFeatureToggleNameInEnvironmentPolicy = new UniqueFeatureToggleNameInEnvironmentPolicy(featureToggleQueryRepository);
        return new FeatureTogglePolicyFacade(uniqueFeatureToggleNameInEnvironmentPolicy);
    }



}
