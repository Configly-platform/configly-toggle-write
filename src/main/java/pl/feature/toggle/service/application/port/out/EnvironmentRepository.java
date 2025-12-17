package pl.feature.toggle.service.application.port.out;

import com.ftaas.domain.environment.EnvironmentId;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;

import java.util.Optional;

public interface EnvironmentRepository {

    Optional<EnvironmentSnapshot> findById(EnvironmentId environmentId);

    void save(EnvironmentSnapshot environmentSnapshot);

}
