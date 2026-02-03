package pl.feature.toggle.service.write.application.port.in;

import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleValueCommand;

public interface ChangeFeatureToggleValueUseCase {

    void handle(ChangeFeatureToggleValueCommand command);

}
