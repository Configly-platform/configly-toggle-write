package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import java.util.ArrayList;
import java.util.List;

public class FeatureToggleCommandRepositorySpy implements FeatureToggleCommandRepository {
    private final List<FeatureToggle> saved = new ArrayList<>();
    private final List<FeatureToggleUpdateResult> updated = new ArrayList<>();

    private boolean failOnAnyCall;

    @Override
    public void save(FeatureToggle featureToggle) {
        if (failOnAnyCall) {
            throw new AssertionError("save should not be called");
        }
        saved.add(featureToggle);
    }

    @Override
    public void update(FeatureToggleUpdateResult updateResult) {
        if (failOnAnyCall) {
            throw new AssertionError("update should not be called");
        }
        updated.add(updateResult);
    }

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        saved.clear();
        updated.clear();
        failOnAnyCall = false;
    }

    public int saveCalls() {
        return saved.size();
    }

    public int updateCalls() {
        return updated.size();
    }

    public FeatureToggle lastSaved() {
        return saved.isEmpty() ? null : saved.getLast();
    }

    public FeatureToggleUpdateResult lastUpdated() {
        return updated.isEmpty() ? null : updated.getLast();
    }

    public List<FeatureToggleUpdateResult> allUpdated() {
        return updated;
    }
}
