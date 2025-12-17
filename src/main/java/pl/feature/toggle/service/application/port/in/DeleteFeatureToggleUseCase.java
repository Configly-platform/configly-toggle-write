package pl.feature.toggle.service.application.port.in;

import com.ftaas.domain.featuretoggle.FeatureToggleId;

public interface DeleteFeatureToggleUseCase {

    void execute(FeatureToggleId featureToggleId);

}
