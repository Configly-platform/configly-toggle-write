package pl.feature.toggle.service.domain.featuretoggle.exception;


import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;

public class FeatureToggleAlreadyExistsException extends RuntimeException {
    public FeatureToggleAlreadyExistsException(FeatureToggleName featureToggleName) {
        super("Feature toggle already exists: " + featureToggleName.value());
    }
}
