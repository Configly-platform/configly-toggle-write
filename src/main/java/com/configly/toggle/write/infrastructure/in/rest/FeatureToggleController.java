package com.configly.toggle.write.infrastructure.in.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.configly.web.model.actor.ActorProvider;
import com.configly.web.model.correlation.CorrelationProvider;
import com.configly.toggle.write.application.port.in.ChangeFeatureToggleStatusUseCase;
import com.configly.toggle.write.application.port.in.ChangeFeatureToggleValueUseCase;
import com.configly.toggle.write.application.port.in.CreateFeatureToggleUseCase;
import com.configly.toggle.write.application.port.in.UpdateFeatureToggleUseCase;
import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleStatusCommand;
import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleValueCommand;
import com.configly.toggle.write.application.port.in.command.CreateFeatureToggleCommand;
import com.configly.toggle.write.application.port.in.command.UpdateFeatureToggleCommand;
import com.configly.toggle.write.infrastructure.in.rest.dto.ChangeFeatureToggleValueDto;
import com.configly.toggle.write.infrastructure.in.rest.dto.CreateFeatureToggleDto;
import com.configly.toggle.write.infrastructure.in.rest.dto.UpdateFeatureToggleDto;

import java.util.UUID;


@AllArgsConstructor
@RestController
@RequestMapping("/rest/api/projects/{projectId}/environments/{environmentId}/feature-toggles")
class FeatureToggleController {

    private final CreateFeatureToggleUseCase createFeatureToggleUseCase;
    private final UpdateFeatureToggleUseCase updateFeatureToggleUseCase;
    private final ChangeFeatureToggleValueUseCase changeFeatureToggleValueUseCase;
    private final ChangeFeatureToggleStatusUseCase changeFeatureToggleStatusUseCase;
    private final ActorProvider actorProvider;
    private final CorrelationProvider correlationProvider;

    @PostMapping
    UUID create(
            @PathVariable String projectId,
            @PathVariable String environmentId,
            @RequestBody @Valid CreateFeatureToggleDto dto
    ) {
        var command = CreateFeatureToggleCommand.from(projectId, environmentId, dto,
                actorProvider.current(), correlationProvider.current());
        return createFeatureToggleUseCase.execute(command).uuid();
    }

    @PutMapping("/{featureToggleId}")
    void update(
            @PathVariable String projectId,
            @PathVariable String environmentId,
            @PathVariable String featureToggleId,
            @RequestBody @Valid UpdateFeatureToggleDto dto
    ) {
        var command = UpdateFeatureToggleCommand.from(featureToggleId, projectId, environmentId, dto,
                actorProvider.current(), correlationProvider.current());
        updateFeatureToggleUseCase.execute(command);
    }

    @PutMapping("/{featureToggleId}/status")
    void changeStatus(
            @PathVariable String projectId,
            @PathVariable String environmentId,
            @PathVariable String featureToggleId,
            @RequestBody @NotEmpty String status
    ) {
        var command = ChangeFeatureToggleStatusCommand.create(projectId, environmentId, featureToggleId, status,
                actorProvider.current(), correlationProvider.current());
        changeFeatureToggleStatusUseCase.handle(command);
    }

    @PutMapping("/{featureToggleId}/value")
    void changeValue(
            @PathVariable String projectId,
            @PathVariable String environmentId,
            @PathVariable String featureToggleId,
            @RequestBody @Valid ChangeFeatureToggleValueDto dto
    ) {
        var command = ChangeFeatureToggleValueCommand.from(projectId, environmentId, featureToggleId, dto,
                actorProvider.current(), correlationProvider.current());
        changeFeatureToggleValueUseCase.handle(command);
    }

}
