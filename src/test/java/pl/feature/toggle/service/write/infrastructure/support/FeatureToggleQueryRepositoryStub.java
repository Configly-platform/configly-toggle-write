package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.write.StubSupport;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;

import static pl.feature.toggle.service.write.StubSupport.forMethod;

public class FeatureToggleQueryRepositoryStub implements FeatureToggleQueryRepository {
    private final StubSupport<FeatureToggle> getOrThrow =
            forMethod("getOrThrow(FeatureToggleId)");
    private final StubSupport<Boolean> exists =
            forMethod("exists(FeatureToggleName, EnvironmentId)");

    private boolean failOnAnyCall = false;

    public void getOrThrowReturns(FeatureToggle featureToggle) {
        getOrThrow.willReturn(featureToggle);
    }

    public void getOrThrowThrows(RuntimeException ex) {
        getOrThrow.willThrow(ex);
    }

    public void existsReturns(boolean value) {
        exists.willReturn(value);
    }

    public void existsThrows(RuntimeException ex) {
        exists.willThrow(ex);
    }

    public void expectNoCalls() {
        failOnAnyCall = true;
    }

    public void reset() {
        getOrThrow.reset();
        exists.reset();
    }

    @Override
    public FeatureToggle getOrThrow(FeatureToggleId featureToggleId) {
        if (failOnAnyCall) {
            throw new AssertionError("getOrThrow should not be called");
        }
        return getOrThrow.get();
    }

    @Override
    public boolean exists(FeatureToggleName featureToggleName, EnvironmentId environmentId) {
        if (failOnAnyCall) {
            throw new AssertionError("exists should not be called");
        }
        return exists.get();
    }
}
