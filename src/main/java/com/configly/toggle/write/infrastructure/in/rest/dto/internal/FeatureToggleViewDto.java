package com.configly.toggle.write.infrastructure.in.rest.dto.internal;

import java.time.LocalDateTime;

public record FeatureToggleViewDto(
        String id,
        String environmentId,
        String name,
        String description,
        String value,
        String type,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long revision
) {
}
