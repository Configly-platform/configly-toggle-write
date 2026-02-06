package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

public interface FeatureToggleCommandRepository {

    void save(FeatureToggle featureToggle);

    void update(FeatureToggleUpdateResult updateResult);
}
