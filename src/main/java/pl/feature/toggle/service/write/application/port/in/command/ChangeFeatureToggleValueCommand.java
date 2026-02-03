package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.ChangeFeatureToggleValueDto;

public record ChangeFeatureToggleValueCommand(
        FeatureToggleId featureToggleId,
        String newValue,
        String newValueType
) {

    public static ChangeFeatureToggleValueCommand from(String featureToggleId, ChangeFeatureToggleValueDto dto) {
        return new ChangeFeatureToggleValueCommand(FeatureToggleId.create(featureToggleId), dto.value(), dto.type());
    }
}
