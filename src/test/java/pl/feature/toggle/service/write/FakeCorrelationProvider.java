package pl.feature.toggle.service.write;

import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;

public class FakeCorrelationProvider implements CorrelationProvider {
    @Override
    public CorrelationId current() {
        return CorrelationId.of("test");
    }
}
