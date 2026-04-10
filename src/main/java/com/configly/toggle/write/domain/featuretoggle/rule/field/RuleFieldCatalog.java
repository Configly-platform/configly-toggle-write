package com.configly.toggle.write.domain.featuretoggle.rule.field;

import com.configly.toggle.write.domain.featuretoggle.rule.FieldRef;
import com.configly.toggle.write.domain.featuretoggle.rule.FieldType;

import java.util.Map;
import java.util.Set;

import static com.configly.toggle.write.domain.featuretoggle.rule.FieldType.CONTEXT;
import static com.configly.toggle.write.domain.featuretoggle.rule.Operator.EQUALS;
import static com.configly.toggle.write.domain.featuretoggle.rule.Operator.NOT_EQUALS;
import static com.configly.toggle.write.domain.featuretoggle.rule.field.RuleFieldCatalog.RuleFieldKey.contextKey;
import static com.configly.toggle.write.domain.featuretoggle.rule.field.RuleFieldDefinition.contextFieldDefinition;
import static com.configly.value.toggle.FeatureToggleValueType.TEXT;

public final class RuleFieldCatalog {

    private final Map<RuleFieldKey, RuleFieldDefinition> fields = Map.of(
            contextKey("userId"),
            contextFieldDefinition(
                    "userId",
                    TEXT,
                    Set.of(EQUALS, NOT_EQUALS)
            ),

            contextKey("username"),
            contextFieldDefinition(
                    "username",
                    TEXT,
                    Set.of(EQUALS, NOT_EQUALS)
            )
    );

    public RuleFieldDefinition getOrThrow(FieldRef ref) {
        var definition = fields.get(new RuleFieldKey(ref.type(), ref.key()));
        if (definition == null) {
            throw new RuntimeException("Not found definition for key: " + ref.key());
        }
        return definition;
    }


    public record RuleFieldKey(
            FieldType type,
            String key
    ) {

        static RuleFieldKey contextKey(String key) {
            return new RuleFieldKey(CONTEXT, key);
        }
    }

}
