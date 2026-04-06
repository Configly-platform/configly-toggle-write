package com.configly.toggle.write;

import com.configly.web.model.correlation.CorrelationId;
import com.configly.web.model.correlation.CorrelationProvider;

public class FakeCorrelationProvider implements CorrelationProvider {
    @Override
    public CorrelationId current() {
        return CorrelationId.of("test");
    }
}
