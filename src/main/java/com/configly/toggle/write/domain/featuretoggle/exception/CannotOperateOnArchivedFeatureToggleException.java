package com.configly.toggle.write.domain.featuretoggle.exception;

import com.configly.model.featuretoggle.FeatureToggleName;

public class CannotOperateOnArchivedFeatureToggleException extends RuntimeException {
    public CannotOperateOnArchivedFeatureToggleException(FeatureToggleName toggleName) {
        super(String.format("Cannot operate on archived feature toggle: %s", toggleName.value()));
    }
}
