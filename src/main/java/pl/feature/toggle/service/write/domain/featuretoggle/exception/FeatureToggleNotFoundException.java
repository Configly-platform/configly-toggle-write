package pl.feature.toggle.service.write.domain.featuretoggle.exception;


import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

public class FeatureToggleNotFoundException extends RuntimeException {
    public FeatureToggleNotFoundException(FeatureToggleId featureToggleId) {
        super("Feature toggle not found: " + featureToggleId.uuid());
    }
}
