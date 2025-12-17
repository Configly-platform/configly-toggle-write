package pl.feature.toggle.service.domain.environment.exception;

import com.ftaas.domain.environment.EnvironmentId;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(EnvironmentId environmentId) {
        super("Environment not found: " + environmentId.uuid());
    }
}
