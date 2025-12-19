package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.write.application.port.in.command.UpdateFeatureToggleCommand;

public interface UpdateFeatureToggleUseCase {

    void execute(UpdateFeatureToggleCommand command);

}
