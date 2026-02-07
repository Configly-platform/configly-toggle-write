package pl.feature.toggle.service.write.application.policy;

import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;

@AllArgsConstructor
class UniqueFeatureToggleNameInEnvironmentPolicy {

    private final FeatureToggleQueryRepository repo;

    void ensure(FeatureToggleName name, EnvironmentId environmentId) {
        var nameIsNotUnique = repo.exists(name, environmentId);
        if (nameIsNotUnique) {
            throw new FeatureToggleAlreadyExistsException(name);
        }
    }
}
