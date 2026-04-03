package com.configly.toggle.write.application.port.in;

import com.configly.toggle.write.application.port.in.command.UpdateFeatureToggleCommand;

public interface UpdateFeatureToggleUseCase {

    void execute(UpdateFeatureToggleCommand command);

}
