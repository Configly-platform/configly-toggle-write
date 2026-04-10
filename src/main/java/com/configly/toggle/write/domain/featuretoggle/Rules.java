package com.configly.toggle.write.domain.featuretoggle;

import com.configly.toggle.write.domain.featuretoggle.rule.Rule;

import java.util.List;

public record Rules(
        List<Rule> rules
) {

    public static Rules empty() {
        return new Rules(List.of());
    }
}
