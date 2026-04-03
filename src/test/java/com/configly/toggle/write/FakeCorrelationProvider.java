package com.configly.toggle.write;

import com.configly.web.correlation.CorrelationId;
import com.configly.web.correlation.CorrelationProvider;

public class FakeCorrelationProvider implements CorrelationProvider {
    @Override
    public CorrelationId current() {
        return CorrelationId.of("test");
    }
}
