package com.configly.toggle.write.application.port.in;

import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleValueCommand;

public interface ChangeFeatureToggleValueUseCase {

    void handle(ChangeFeatureToggleValueCommand command);

}
