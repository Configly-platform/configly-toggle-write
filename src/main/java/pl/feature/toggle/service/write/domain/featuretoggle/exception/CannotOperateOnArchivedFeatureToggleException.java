package pl.feature.toggle.service.write.domain.featuretoggle.exception;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;

public class CannotOperateOnArchivedFeatureToggleException extends RuntimeException {
    public CannotOperateOnArchivedFeatureToggleException(FeatureToggleName toggleName) {
        super(String.format("Cannot operate on archived feature toggle: %s", toggleName.value()));
    }
}
