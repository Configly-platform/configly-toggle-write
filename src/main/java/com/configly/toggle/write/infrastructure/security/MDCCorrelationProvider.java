package com.configly.toggle.write.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.web.model.correlation.CorrelationProvider;

@AllArgsConstructor
@Slf4j
class MDCCorrelationProvider implements CorrelationProvider {


    @Override
    public CorrelationId current() {
        return CorrelationId.of(MDC.get(CorrelationId.MDCName()));
    }
}
