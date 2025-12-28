package pl.feature.toggle.service.write.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;

@AllArgsConstructor
class HttpCorrelationProvider implements CorrelationProvider {

    private static final String HEADER = "X-Correlation-Id";

    private final HttpServletRequest request;

    @Override
    public CorrelationId current() {
        var value = request.getHeader(HEADER);
        return CorrelationId.of(value);
    }
}
