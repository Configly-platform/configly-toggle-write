package pl.feature.toggle.service.write.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotEmpty;

public record ChangeFeatureToggleValueDto(
        @NotEmpty String value
) {
}
