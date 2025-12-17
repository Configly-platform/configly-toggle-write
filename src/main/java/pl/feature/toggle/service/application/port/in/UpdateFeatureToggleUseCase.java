package pl.feature.toggle.service.application.port.in;

import pl.feature.toggle.service.application.port.in.command.UpdateFeatureToggleCommand;

public interface UpdateFeatureToggleUseCase {

    void execute(UpdateFeatureToggleCommand command);

}
