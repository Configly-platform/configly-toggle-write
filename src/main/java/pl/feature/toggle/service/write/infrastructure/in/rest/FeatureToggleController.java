package pl.feature.toggle.service.write.infrastructure.in.rest;

import pl.feature.toggle.service.write.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.UpdateFeatureToggleCommand;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.FeatureToggleSnapshotDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

import java.util.UUID;


@AllArgsConstructor
@RestController
@RequestMapping("/rest/api/feature-toggles/write")
class FeatureToggleController {

    private final CreateFeatureToggleUseCase createFeatureToggleUseCase;
    private final UpdateFeatureToggleUseCase updateFeatureToggleUseCase;
    private final DeleteFeatureToggleUseCase deleteFeatureToggleUseCase;

    @PostMapping
    UUID create(@RequestBody @Valid FeatureToggleSnapshotDto dto) {
        var command = CreateFeatureToggleCommand.from(dto);
        return createFeatureToggleUseCase.execute(command).uuid();
    }

    @PutMapping("/{featureToggleId}")
    void update(@PathVariable String featureToggleId, @RequestBody @Valid FeatureToggleSnapshotDto dto) {
        var command = UpdateFeatureToggleCommand.from(featureToggleId, dto);
        updateFeatureToggleUseCase.execute(command);
    }

    @DeleteMapping("/{featureToggleId}")
    void delete(@PathVariable String featureToggleId) {
        deleteFeatureToggleUseCase.execute(FeatureToggleId.create(featureToggleId));
    }


}
