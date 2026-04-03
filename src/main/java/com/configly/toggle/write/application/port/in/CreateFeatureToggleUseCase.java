package com.configly.toggle.write.application.port.in;

import com.configly.toggle.write.application.port.in.command.CreateFeatureToggleCommand;
import com.configly.model.featuretoggle.FeatureToggleId;

public interface CreateFeatureToggleUseCase {

    FeatureToggleId execute(CreateFeatureToggleCommand command);
}
