package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleStatus;

public record ChangeFeatureToggleStatusCommand(
        FeatureToggleId featureToggleId,
        FeatureToggleStatus newStatus
) {

    public static ChangeFeatureToggleStatusCommand create(String featureToggleId, String newStatus) {
        return new ChangeFeatureToggleStatusCommand(FeatureToggleId.create(featureToggleId), FeatureToggleStatus.valueOf(newStatus));
    }
}
