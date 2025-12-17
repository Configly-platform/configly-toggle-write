package pl.feature.toggle.service.application.port.in;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.application.port.in.command.CreateFeatureToggleCommand;

public interface CreateFeatureToggleUseCase {

    FeatureToggleId execute(CreateFeatureToggleCommand command);
}
