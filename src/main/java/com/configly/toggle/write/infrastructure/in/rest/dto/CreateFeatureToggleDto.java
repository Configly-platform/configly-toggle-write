package com.configly.toggle.write.infrastructure.in.rest.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CreateFeatureToggleDto(
        @NotNull
        String name,
        @Nullable
        String description,
        @NotNull
        String value,
        @NotNull
        String type
) {
}
