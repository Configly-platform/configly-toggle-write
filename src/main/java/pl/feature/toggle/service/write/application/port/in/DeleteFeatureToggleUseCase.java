package pl.feature.toggle.service.write.application.port.in;


import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

public interface DeleteFeatureToggleUseCase {

    void execute(FeatureToggleId featureToggleId);

}
