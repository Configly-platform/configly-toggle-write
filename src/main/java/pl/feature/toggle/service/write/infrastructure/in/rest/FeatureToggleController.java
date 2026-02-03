package pl.feature.toggle.service.write.infrastructure.in.rest;

import jakarta.validation.constraints.NotEmpty;
import pl.feature.toggle.service.write.application.port.in.ChangeFeatureToggleStatusUseCase;
import pl.feature.toggle.service.write.application.port.in.ChangeFeatureToggleValueUseCase;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleStatusCommand;
import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleValueCommand;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.command.UpdateFeatureToggleCommand;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.ChangeFeatureToggleValueDto;
import pl.feature.toggle.service.write.infrastructure.in.rest.dto.CreateFeatureToggleDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@AllArgsConstructor
@RestController
@RequestMapping("/rest/api/projects/{projectId}/environments/{environmentId}/feature-toggles")
class FeatureToggleController {

    private final CreateFeatureToggleUseCase createFeatureToggleUseCase;
    private final UpdateFeatureToggleUseCase updateFeatureToggleUseCase;
    private final ChangeFeatureToggleValueUseCase changeFeatureToggleValueUseCase;
    private final ChangeFeatureToggleStatusUseCase changeFeatureToggleStatusUseCase;

    @PostMapping
    UUID create(@RequestBody @Valid CreateFeatureToggleDto dto) {
        var command = CreateFeatureToggleCommand.from(dto);
        return createFeatureToggleUseCase.execute(command).uuid();
    }

    @PutMapping("/{featureToggleId}")
    void update(
            @PathVariable String projectId,
            @PathVariable String environmentId,
            @PathVariable String featureToggleId,
            @RequestBody @Valid CreateFeatureToggleDto dto) {
        var command = UpdateFeatureToggleCommand.from(featureToggleId, dto);
        updateFeatureToggleUseCase.execute(command);
    }

    @PutMapping("/{featureToggleId}/status")
    void changeStatus(@PathVariable String featureToggleId, @RequestBody @NotEmpty String status){
        var command = ChangeFeatureToggleStatusCommand.create(featureToggleId, status);
        changeFeatureToggleStatusUseCase.handle(command);
    }

    @PutMapping("/{featureToggleId}/value")
    void changeType(@PathVariable String featureToggleId, @RequestBody @Valid ChangeFeatureToggleValueDto dto){
        var command = ChangeFeatureToggleValueCommand.from(featureToggleId,dto);
        changeFeatureToggleValueUseCase.handle(command);
    }

}
