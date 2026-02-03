package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;

public interface FeatureToggleCommandRepository {

    void save(FeatureToggle featureToggle);

    void update(FeatureToggle featureToggle);
}
