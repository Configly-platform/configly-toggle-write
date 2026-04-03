package com.configly.toggle.write.domain.reference.exception;

import com.configly.model.environment.EnvironmentId;

public class CannotOperateOnArchivedEnvironmentException extends RuntimeException {
    public CannotOperateOnArchivedEnvironmentException(EnvironmentId environmentId) {
        super(String.format("Cannot operate on archived environment: %s", environmentId.idAsString()));
    }
}
