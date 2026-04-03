package com.configly.toggle.write.domain.featuretoggle.exception;


import com.configly.model.featuretoggle.FeatureToggleName;

public class FeatureToggleAlreadyExistsException extends RuntimeException {
    public FeatureToggleAlreadyExistsException(FeatureToggleName featureToggleName) {
        super("Feature toggle already exists: " + featureToggleName.value());
    }
}
