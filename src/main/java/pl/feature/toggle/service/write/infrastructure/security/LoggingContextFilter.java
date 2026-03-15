package pl.feature.toggle.service.write.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.feature.toggle.service.model.security.actor.ActorProvider;
import pl.feature.toggle.service.model.security.correlation.CorrelationProvider;

import java.io.IOException;

@Component
@AllArgsConstructor
class LoggingContextFilter extends OncePerRequestFilter {

    private final CorrelationProvider correlationProvider;
    private final ActorProvider actorProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            MDC.put("correlationId", correlationProvider.current().value());
            MDC.put("actor", actorProvider.current().idAsString());
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
