package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeInMemoryEnvironmentRefRepository implements EnvironmentRefRepository {

    private final Map<EnvironmentId, EnvironmentRef> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<EnvironmentRef> find(ProjectId projectId, EnvironmentId environmentId) {
        var ref = storage.get(environmentId);
        if (ref == null) {
            return Optional.empty();
        }
        if (!ref.projectId().equals(projectId)) {
            return Optional.empty();
        }
        return Optional.of(ref);
    }

    @Override
    public Optional<EnvironmentRef> findConsistent(ProjectId projectId, EnvironmentId environmentId) {
        var refOpt = find(projectId, environmentId);
        if (refOpt.isEmpty()) {
            return Optional.empty();
        }
        var ref = refOpt.get();
        return ref.consistent() ? Optional.of(ref) : Optional.empty();
    }

    @Override
    public void insert(EnvironmentRef ref) {
        var key = ref.environmentId();
        if (storage.containsKey(key)) {
            throw new AssertionError("insert called but EnvironmentRef already exists: " + key);
        }
        storage.put(key, ref);
    }

    @Override
    public void update(EnvironmentRef ref) {
        var key = ref.environmentId();
        if (!storage.containsKey(key)) {
            throw new AssertionError("update called but EnvironmentRef does not exist: " + key);
        }
        storage.put(key, ref);
    }

    @Override
    public void upsert(EnvironmentRef ref) {
        storage.put(ref.environmentId(), ref);
    }

    @Override
    public boolean markInconsistentIfNotMarked(EnvironmentId environmentId) {
        var existing = storage.get(environmentId);
        if (existing == null) {
            return false;
        }
        if (!existing.consistent()) {
            return false;
        }

        storage.put(environmentId, markInconsistent(existing));
        return true;
    }

    private EnvironmentRef markInconsistent(EnvironmentRef environmentRef) {
        return new EnvironmentRef(
                environmentRef.environmentId(),
                environmentRef.projectId(),
                environmentRef.status(),
                environmentRef.lastRevision(),
                false
        );
    }
}
