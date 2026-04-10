package com.configly.toggle.write.application.handler;

import com.configly.outbox.api.OutboxWriter;
import com.configly.toggle.write.application.port.in.ChangeFeatureToggleRulesUseCase;
import com.configly.toggle.write.application.port.in.EnvironmentRefConsistency;
import com.configly.toggle.write.application.port.in.ProjectRefConsistency;
import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleRulesCommand;
import com.configly.toggle.write.application.port.out.FeatureToggleCommandRepository;
import com.configly.toggle.write.application.port.out.FeatureToggleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
final class ChangeFeatureToggleRulesHandler implements ChangeFeatureToggleRulesUseCase {

    private final FeatureToggleCommandRepository toggleCommandRepository;
    private final FeatureToggleQueryRepository toggleQueryRepository;
    private final ProjectRefConsistency projectRefConsistency;
    private final EnvironmentRefConsistency environmentRefConsistency;
    private final OutboxWriter outboxWriter;


    @Override
    public void handle(ChangeFeatureToggleRulesCommand command) {
        var environmentRef = environmentRefConsistency.getTrusted(command.projectId(), command.environmentId());
        environmentRef.assertIsActive();
        var projectRef = projectRefConsistency.getTrusted(command.projectId());
        projectRef.assertIsActive();
        var featureToggle = toggleQueryRepository.getOrThrow(command.featureToggleId());

        featureToggle.changeRules(command.rules());
        // TODO
    }
}
