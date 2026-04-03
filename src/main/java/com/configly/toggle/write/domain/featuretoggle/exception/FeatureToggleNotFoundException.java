package com.configly.toggle.write.domain.featuretoggle.exception;


import com.configly.model.featuretoggle.FeatureToggleId;

public class FeatureToggleNotFoundException extends RuntimeException {
    public FeatureToggleNotFoundException(FeatureToggleId featureToggleId) {
        super("Feature toggle not found: " + featureToggleId.uuid());
    }
}
