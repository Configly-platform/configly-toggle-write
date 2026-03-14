package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;

public interface ArchiveFeatureTogglesByEnvironmentUseCase {

    void handle(ArchiveFeatureTogglesByEnvironmentCommand command);
}
