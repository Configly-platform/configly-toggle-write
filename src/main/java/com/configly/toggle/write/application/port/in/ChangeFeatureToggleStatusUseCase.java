package com.configly.toggle.write.application.port.in;

import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleStatusCommand;

public interface ChangeFeatureToggleStatusUseCase {

    void handle(ChangeFeatureToggleStatusCommand command);

}
