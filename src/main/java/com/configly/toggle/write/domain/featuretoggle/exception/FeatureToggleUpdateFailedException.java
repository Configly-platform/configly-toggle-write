package com.configly.toggle.write.domain.featuretoggle.exception;

import com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult;

public class FeatureToggleUpdateFailedException extends RuntimeException {
    public FeatureToggleUpdateFailedException(FeatureToggleUpdateResult updateResult) {
        super(String.format("Feature toggle update failed for id: [%s] current revision: [%s] expected revision: [%s]",
                updateResult.featureToggle().id().uuid(),
                updateResult.featureToggle().revision().value(),
                updateResult.expectedRevision().value()));
    }
}
