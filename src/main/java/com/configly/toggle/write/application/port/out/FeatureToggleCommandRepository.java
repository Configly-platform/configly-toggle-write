package com.configly.toggle.write.application.port.out;

import com.configly.toggle.write.domain.featuretoggle.FeatureToggle;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult;

public interface FeatureToggleCommandRepository {

    void save(FeatureToggle featureToggle);

    void update(FeatureToggleUpdateResult updateResult);
}
