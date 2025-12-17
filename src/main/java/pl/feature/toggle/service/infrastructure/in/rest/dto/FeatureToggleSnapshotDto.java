package pl.feature.toggle.service.infrastructure.in.rest.dto;

import com.ftaas.domain.featuretoggle.FeatureToggleType;
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
        FeatureToggleType type,
        @NotNull
        String value
) {
}
