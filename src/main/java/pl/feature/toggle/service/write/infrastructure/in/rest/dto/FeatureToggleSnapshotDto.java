package pl.feature.toggle.service.write.infrastructure.in.rest.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleType;

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
        FeatureToggleType type,
        @NotNull
        String value
) {
}
