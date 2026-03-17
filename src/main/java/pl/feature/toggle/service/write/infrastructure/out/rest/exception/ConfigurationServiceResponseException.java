package pl.feature.toggle.service.write.infrastructure.out.rest.exception;

import java.util.Map;

public class ConfigurationServiceResponseException extends RuntimeException {

    private final Map<String, Object> context;

    public ConfigurationServiceResponseException(String message, Map<String, Object> context) {
        super(message);
        this.context = context;
    }

    public Map<String, Object> context() {
        return context;
    }
}
