package com.configly.toggle.write.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.configly.web.model.actor.ActorProvider;
import com.configly.web.model.correlation.CorrelationId;

import java.io.IOException;

@Component
@AllArgsConstructor
class LoggingContextFilter extends OncePerRequestFilter {

    private final ActorProvider actorProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            var value = request.getHeader(CorrelationId.headerName());
            var correlation = CorrelationId.of(value);
            MDC.put(CorrelationId.MDCName(), correlation.value());

            var actor = actorProvider.current();
            if (actor != null) {
                MDC.put("actor", actor.idAsString());
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
