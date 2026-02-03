package pl.feature.toggle.service.write.infrastructure.in.rest.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record UpdateFeatureToggleDto(
        @NotNull
        String name,
        @Nullable
        String description
) {
}
