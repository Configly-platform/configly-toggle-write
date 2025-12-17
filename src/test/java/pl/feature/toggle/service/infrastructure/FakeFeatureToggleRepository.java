package pl.feature.toggle.service.infrastructure;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import com.ftaas.domain.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeFeatureToggleRepository implements FeatureToggleRepository {

    private final Map<FeatureToggleId, FeatureToggle> featureToggles = new HashMap<>();

    @Override
    public void save(FeatureToggle featureToggle) {
        featureToggles.put(featureToggle.id(), featureToggle);
    }

    @Override
    public Optional<FeatureToggle> findById(FeatureToggleId featureToggleId) {
        return Optional.ofNullable(featureToggles.get(featureToggleId));
    }

    @Override
    public void update(FeatureToggle featureToggle) {
        featureToggles.put(featureToggle.id(), featureToggle);
    }

    @Override
    public void delete(FeatureToggle featureToggle) {
        featureToggles.remove(featureToggle.id());
    }

    @Override
    public boolean exists(FeatureToggleId featureToggleId) {
        return featureToggles.containsKey(featureToggleId);
    }

    @Override
    public boolean exists(FeatureToggleName name) {
        return featureToggles.values().stream().anyMatch(ft -> ft.name().equals(name));
    }

    public void clear() {
        featureToggles.clear();
    }
}
