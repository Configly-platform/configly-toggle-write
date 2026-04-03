package com.configly.toggle.write.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggle;
import com.configly.model.featuretoggle.FeatureToggleId;

import java.util.List;

public interface FeatureToggleQueryRepository {

    FeatureToggle getOrThrow(FeatureToggleId featureToggleId);

    List<FeatureToggle> findByEnvironmentId(EnvironmentId environmentId);
}
