package com.configly.toggle.write.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import com.configly.web.model.actor.*;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class JwtActorProvider implements ActorProvider {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String PREFERRED_USERNAME = "preferred_username";

    @Override
    public Actor current() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return Actor.system();
        }

        var actorId = ActorId.create(jwt.getSubject());
        var username = Username.create(jwt.getClaimAsString(PREFERRED_USERNAME));

        var roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(this::toDomainRole)
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());

        return Actor.create(actorId, username, roles);
    }

    private Optional<Role> toDomainRole(String authority) {
        String normalized = authority.startsWith(ROLE_PREFIX)
                ? authority.substring(ROLE_PREFIX.length())
                : authority;

        return Role.fromString(normalized);
    }
}
