package com.configly.toggle.write.domain.featuretoggle.rule;

import com.configly.value.rule.condition.RuleConditionValue;
import com.configly.value.toggle.FeatureToggleValue;

public record Rule(
        RulePriority priority,
        FieldRef field,
        Operator operator,
        RuleConditionValue matchValue,
        FeatureToggleValue resultValue
) {
}
