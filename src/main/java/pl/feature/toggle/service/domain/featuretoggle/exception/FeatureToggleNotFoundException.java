package pl.feature.toggle.service.domain.featuretoggle.exception;

import com.ftaas.domain.featuretoggle.FeatureToggleId;

public class FeatureToggleNotFoundException extends RuntimeException {
    public FeatureToggleNotFoundException(FeatureToggleId featureToggleId) {
        super("Feature toggle not found: " + featureToggleId.uuid());
    }
}
