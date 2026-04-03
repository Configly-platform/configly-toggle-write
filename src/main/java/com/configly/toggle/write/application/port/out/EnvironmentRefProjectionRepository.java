package com.configly.toggle.write.application.port.out;

import com.configly.model.environment.EnvironmentId;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

public interface EnvironmentRefProjectionRepository {
    void insert(EnvironmentRef ref);

    void update(EnvironmentRef ref);

    void upsert(EnvironmentRef ref);

    boolean markInconsistentIfNotMarked(EnvironmentId environmentId);

}
