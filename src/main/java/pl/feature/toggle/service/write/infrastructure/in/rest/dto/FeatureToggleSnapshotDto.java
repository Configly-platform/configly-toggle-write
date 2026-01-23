package pl.feature.toggle.service.write.infrastructure.in.rest.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record FeatureToggleSnapshotDto(
        @NotNull
        String projectId,
        @NotNull
        String environmentId,
        @NotNull
        String name,
        @Nullable
        String description,
        @NotNull
        Object value
) {
}
