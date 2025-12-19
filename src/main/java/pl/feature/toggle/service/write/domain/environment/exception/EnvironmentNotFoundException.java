package pl.feature.toggle.service.write.domain.environment.exception;


import pl.feature.toggle.service.model.environment.EnvironmentId;

public class EnvironmentNotFoundException extends RuntimeException {
    public EnvironmentNotFoundException(EnvironmentId environmentId) {
        super("Environment not found: " + environmentId.uuid());
    }
}
