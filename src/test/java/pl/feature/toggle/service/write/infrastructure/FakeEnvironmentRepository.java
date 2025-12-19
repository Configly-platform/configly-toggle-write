package pl.feature.toggle.service.write.infrastructure;

import pl.feature.toggle.service.write.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.model.environment.EnvironmentId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeEnvironmentRepository implements EnvironmentRepository {

    private final Map<EnvironmentId, EnvironmentSnapshot> environments = new HashMap<>();

    @Override
    public Optional<EnvironmentSnapshot> findById(EnvironmentId environmentId) {
        return Optional.ofNullable(environments.get(environmentId));
    }

    @Override
    public void save(EnvironmentSnapshot environmentSnapshot) {
        environments.put(environmentSnapshot.id(), environmentSnapshot);
    }

    public void clear() {
        environments.clear();
    }
}
