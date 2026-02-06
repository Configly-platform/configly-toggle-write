package pl.feature.toggle.service.write.domain.reference.exception;

import pl.feature.toggle.service.model.environment.EnvironmentId;

public class CannotOperateOnArchivedEnvironmentException extends RuntimeException {
    public CannotOperateOnArchivedEnvironmentException(EnvironmentId environmentId) {
        super(String.format("Cannot operate on archived environment: %s", environmentId.idAsString()));
    }
}
