package com.configly.toggle.write.domain.featuretoggle.rule.field;

import com.configly.toggle.write.domain.featuretoggle.rule.FieldType;
import com.configly.toggle.write.domain.featuretoggle.rule.Operator;
import com.configly.value.toggle.FeatureToggleValueType;

import java.util.Set;

public record RuleFieldDefinition(
        String key,
        FieldType fieldType,
        FeatureToggleValueType valueType,
        Set<Operator> allowedOperators
) {

    static RuleFieldDefinition contextFieldDefinition(String key, FeatureToggleValueType valueType, Set<Operator> allowedOperators) {
        return new RuleFieldDefinition(
                key,
                FieldType.CONTEXT,
                valueType,
                allowedOperators
        );
    }
}
