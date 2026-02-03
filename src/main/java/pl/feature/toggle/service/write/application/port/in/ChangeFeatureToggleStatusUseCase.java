package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleStatusCommand;

public interface ChangeFeatureToggleStatusUseCase {

    void handle(ChangeFeatureToggleStatusCommand command);

}
