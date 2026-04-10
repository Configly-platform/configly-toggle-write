package com.configly.toggle.write.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ChangeFeatureToggleRulesDto(
        List<RuleDto> rules
) {

    public record RuleDto(
            @NotNull
            Integer priority,
            @NotNull
            String field,
            @NotNull
            String operator,
            @NotNull
            String matchValue,
            @NotNull
            String resultValue
    ) {

    }
}
