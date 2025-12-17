package pl.feature.toggle.service.application.port.out;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import com.ftaas.domain.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;

import java.util.Optional;

public interface FeatureToggleRepository {

    void save(FeatureToggle featureToggle);

    Optional<FeatureToggle> findById(FeatureToggleId featureToggleId);

    void update(FeatureToggle featureToggle);

    void delete(FeatureToggle featureToggle);

    boolean exists(FeatureToggleId featureToggleId);

    boolean exists(FeatureToggleName name);
}
