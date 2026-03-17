package pl.feature.toggle.service.write.infrastructure.in.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.internal.FeatureToggleViewDto;

@RestController
@RequestMapping("/internal/feature-toggles")
@AllArgsConstructor
class InternalFeatureToggleController {

    private final FeatureToggleQueryRepository queryRepository;

    @GetMapping("/{featureToggleId}/view")
    public FeatureToggleViewDto getFeatureToggleViewDto(@PathVariable String featureToggleId) {
        var featureToggle = queryRepository.getOrThrow(FeatureToggleId.create(featureToggleId));
        return new FeatureToggleViewDto(
                featureToggle.id().idAsString(),
                featureToggle.environmentId().idAsString(),
                featureToggle.name().value(),
                featureToggle.description().value(),
                featureToggle.value().asText(),
                featureToggle.value().typeName(),
                featureToggle.status().name(),
                featureToggle.createdAt().toLocalDateTime(),
                featureToggle.updatedAt().toLocalDateTime(),
                featureToggle.revision().value()
        );
    }

}
