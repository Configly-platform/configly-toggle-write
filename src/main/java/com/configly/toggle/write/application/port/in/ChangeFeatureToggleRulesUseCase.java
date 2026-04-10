package com.configly.toggle.write.application.port.in;

import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleRulesCommand;

public interface ChangeFeatureToggleRulesUseCase {

    void handle(ChangeFeatureToggleRulesCommand command);

}
