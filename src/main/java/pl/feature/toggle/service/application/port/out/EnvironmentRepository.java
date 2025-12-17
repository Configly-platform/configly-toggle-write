package pl.feature.toggle.service.application.port.out;

import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.model.environment.EnvironmentId;

import java.util.Optional;

public interface EnvironmentRepository {

    Optional<EnvironmentSnapshot> findById(EnvironmentId environmentId);

    void save(EnvironmentSnapshot environmentSnapshot);

}
