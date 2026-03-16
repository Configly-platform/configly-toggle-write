package pl.feature.toggle.service.write.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;

@AllArgsConstructor
@Slf4j
class MDCCorrelationProvider implements CorrelationProvider {


    @Override
    public CorrelationId current() {
        return CorrelationId.of(MDC.get(CorrelationId.MDCName()));
    }
}
