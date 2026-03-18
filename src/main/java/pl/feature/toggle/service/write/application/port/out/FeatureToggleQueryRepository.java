package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;

import java.util.List;
import java.util.Optional;

public interface FeatureToggleQueryRepository {

    FeatureToggle getOrThrow(FeatureToggleId featureToggleId);

    List<FeatureToggle> findByEnvironmentId(EnvironmentId environmentId);
}
