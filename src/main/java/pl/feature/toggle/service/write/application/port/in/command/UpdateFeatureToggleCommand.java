package pl.feature.toggle.service.write.application.port.in.command;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.UpdateFeatureToggleDto;

public record UpdateFeatureToggleCommand(
        FeatureToggleId featureToggleId,
        FeatureToggleName name,
        FeatureToggleDescription description
) {

    public static UpdateFeatureToggleCommand from(String featureToggleId, UpdateFeatureToggleDto dto) {
        return new UpdateFeatureToggleCommand(
                FeatureToggleId.create(featureToggleId),
                FeatureToggleName.create(dto.name()),
                FeatureToggleDescription.create(dto.description()));
    }

}
