package pl.feature.toggle.service.write.infrastructure;

import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;

public class FakeCorrelationProvider implements CorrelationProvider {
    @Override
    public CorrelationId current() {
        return CorrelationId.of("test");
    }
}
