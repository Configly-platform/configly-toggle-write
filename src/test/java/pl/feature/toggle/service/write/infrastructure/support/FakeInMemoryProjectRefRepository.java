package pl.feature.toggle.service.write.infrastructure.support;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeInMemoryProjectRefRepository implements ProjectRefProjectionRepository, ProjectRefQueryRepository {

    private final Map<ProjectId, ProjectRef> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<ProjectRef> find(ProjectId projectId) {
        return Optional.ofNullable(storage.get(projectId));
    }

    @Override
    public Optional<ProjectRef> findConsistent(ProjectId projectId) {
        var ref = storage.get(projectId);
        if (ref == null) {
            return Optional.empty();
        }
        return ref.consistent() ? Optional.of(ref) : Optional.empty();
    }

    @Override
    public void insert(ProjectRef ref) {
        var key = ref.projectId();
        if (storage.containsKey(key)) {
            throw new AssertionError("insert called but ProjectRef already exists: " + key);
        }
        storage.put(key, ref);
    }

    @Override
    public void update(ProjectRef ref) {
        var key = ref.projectId();
        if (!storage.containsKey(key)) {
            throw new AssertionError("update called but ProjectRef does not exist: " + key);
        }
        storage.put(key, ref);
    }

    @Override
    public void upsert(ProjectRef ref) {
        storage.put(ref.projectId(), ref);
    }

    @Override
    public boolean markInconsistentIfNotMarked(ProjectId projectId) {
        var existing = storage.get(projectId);
        if (existing == null) {
            return false;
        }
        if (!existing.consistent()) {
            return false;
        }

        var inconsistent = markInconsistent(existing);

        storage.put(projectId, inconsistent);
        return true;
    }

    public void reset() {
        storage.clear();
    }

    private ProjectRef markInconsistent(ProjectRef projectRef) {
        return new ProjectRef(
                projectRef.projectId(),
                projectRef.status(),
                projectRef.lastRevision(),
                false
        );
    }
}
