package pl.feature.toggle.service.write.infrastructure.out.rest.dto;

public record EnvironmentRefDto(
        String environmentId,
        String projectId,
        String status,
        Long lastRevision
) {
}
