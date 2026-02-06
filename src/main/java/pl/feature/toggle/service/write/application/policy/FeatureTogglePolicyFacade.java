package pl.feature.toggle.service.write.application.policy;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;

@AllArgsConstructor
public class FeatureTogglePolicyFacade {

    private final UniqueFeatureToggleNameInEnvironmentPolicy uniqueFeatureToggleNameInEnvironmentPolicy;

    public static FeatureTogglePolicyFacade create(
            FeatureToggleQueryRepository featureToggleQueryRepository
    ) {
        var uniqueFeatureToggleNameInEnvironmentPolicy = new UniqueFeatureToggleNameInEnvironmentPolicy(featureToggleQueryRepository);
        return new FeatureTogglePolicyFacade(uniqueFeatureToggleNameInEnvironmentPolicy);
    }


    public void ensureCreateAllowed(FeatureToggle featureToggle) {
        uniqueFeatureToggleNameInEnvironmentPolicy.ensure(featureToggle.name(), featureToggle.environmentId());
    }

    public void ensureUpdateAllowed(FeatureToggle featureToggle, FeatureToggleName name) {
        uniqueFeatureToggleNameInEnvironmentPolicy.ensure(name, featureToggle.environmentId());
    }
}
