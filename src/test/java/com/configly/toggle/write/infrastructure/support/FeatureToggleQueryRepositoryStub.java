package com.configly.toggle.write.infrastructure.support;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.toggle.write.StubSupport;
import com.configly.toggle.write.application.port.out.FeatureToggleQueryRepository;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggle;

import java.util.List;

import static com.configly.toggle.write.StubSupport.forMethod;

public class FeatureToggleQueryRepositoryStub implements FeatureToggleQueryRepository {
    private final StubSupport<FeatureToggle> getOrThrow =
            forMethod("getOrThrow(FeatureToggleId)");
    private final StubSupport<List<FeatureToggle>> finByEnvironmentId =
            forMethod("findByEnvironmentId(EnvironmentId)");

    private boolean failOnAnyCall = false;

    public void getOrThrowReturns(FeatureToggle featureToggle) {
        getOrThrow.willReturn(featureToggle);
    }

    public void getOrThrowThrows(RuntimeException ex) {
        getOrThrow.willThrow(ex);
    }

    public void findByEnvironmentIdReturns(List<FeatureToggle> featureToggles) {
        finByEnvironmentId.willReturn(featureToggles);
    }

    public void findByEnvironmentIdThrows(RuntimeException ex) {
        finByEnvironmentId.willThrow(ex);
    }

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        getOrThrow.reset();
        finByEnvironmentId.reset();
    }

    @Override
    public FeatureToggle getOrThrow(FeatureToggleId featureToggleId) {
        if (failOnAnyCall) {
            throw new AssertionError("getOrThrow should not be called");
        }
        return getOrThrow.get();
    }

    @Override
    public List<FeatureToggle> findByEnvironmentId(EnvironmentId environmentId) {
        if (failOnAnyCall) {
            throw new AssertionError("findByEnvironmentId should not be called");
        }
        return finByEnvironmentId.get();
    }
}
