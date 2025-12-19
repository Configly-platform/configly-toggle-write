package pl.feature.toggle.service.write.application.port.out;

import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.model.environment.EnvironmentId;

import java.util.Optional;

public interface EnvironmentRepository {

    Optional<EnvironmentSnapshot> findById(EnvironmentId environmentId);

    void save(EnvironmentSnapshot environmentSnapshot);

}
