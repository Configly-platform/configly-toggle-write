package pl.feature.toggle.service.write.infrastructure.out.rest.dto;

public record ProjectRefDto(
        String projectId,
        String status,
        Long revision
) {
}
