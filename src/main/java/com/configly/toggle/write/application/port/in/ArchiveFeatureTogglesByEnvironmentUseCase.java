package com.configly.toggle.write.application.port.in;

import com.configly.toggle.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;

public interface ArchiveFeatureTogglesByEnvironmentUseCase {

    void handle(ArchiveFeatureTogglesByEnvironmentCommand command);
}
