package pl.feature.toggle.service.write.infrastructure;

import pl.feature.toggle.service.write.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;

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
