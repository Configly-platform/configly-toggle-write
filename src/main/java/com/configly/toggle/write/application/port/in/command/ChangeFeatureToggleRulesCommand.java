package com.configly.toggle.write.application.port.in.command;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.infrastructure.in.rest.dto.ChangeFeatureToggleRulesDto;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;

import java.util.List;

public record ChangeFeatureToggleRulesCommand(
        ProjectId projectId,
        EnvironmentId environmentId,
        FeatureToggleId featureToggleId,
        List<RuleCommand> rules,
        Actor actor,
        CorrelationId correlationId
) {

    public static ChangeFeatureToggleRulesCommand from(
            String projectId,
            String environmentId,
            String featureToggleId,
            ChangeFeatureToggleRulesDto dto,
            Actor actor,
            CorrelationId correlationId
    ) {
        return new ChangeFeatureToggleRulesCommand(
                ProjectId.create(projectId),
                EnvironmentId.create(environmentId),
                FeatureToggleId.create(featureToggleId),
                dto.rules().stream()
                        .map(RuleCommand::from)
                        .toList(),
                actor,
                correlationId
        );
    }

    public record RuleCommand(
            int priority,
            String field,
            String operator,
            String matchValue,
            String resultValue
    ) {

        static RuleCommand from(ChangeFeatureToggleRulesDto.RuleDto ruleDto) {
            return new RuleCommand(
                    ruleDto.priority(),
                    ruleDto.field(),
                    ruleDto.operator(),
                    ruleDto.matchValue(),
                    ruleDto.resultValue()
            );
        }
    }
}
