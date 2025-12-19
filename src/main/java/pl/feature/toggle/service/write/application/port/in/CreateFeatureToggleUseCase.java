package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

public interface CreateFeatureToggleUseCase {

    FeatureToggleId execute(CreateFeatureToggleCommand command);
}
