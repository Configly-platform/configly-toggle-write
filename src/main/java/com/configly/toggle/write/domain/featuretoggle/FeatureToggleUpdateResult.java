package com.configly.toggle.write.domain.featuretoggle;

import com.configly.model.Revision;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult.FeatureToggleFieldChange.fieldChange;

public record FeatureToggleUpdateResult(
        FeatureToggle featureToggle,
        Revision expectedRevision,
        List<FeatureToggleFieldChange> changes
) {

    public static FeatureToggleUpdateResult updated(FeatureToggle featureToggle, Revision expectedRevision, FeatureToggleFieldChange... changes) {
        return new FeatureToggleUpdateResult(featureToggle, expectedRevision, List.of(changes));
    }

    public static FeatureToggleUpdateResult noChanges(FeatureToggle featureToggle) {
        return new FeatureToggleUpdateResult(featureToggle, featureToggle.revision(), List.of());
    }

    public static FeatureToggleUpdateResult of(FeatureToggle featureToggle, Revision expectedRevision, List<FeatureToggleFieldChange> changes) {
        return new FeatureToggleUpdateResult(featureToggle, expectedRevision, changes);
    }

    public boolean wasUpdated() {
        return !changes.isEmpty();
    }

    public record FeatureToggleFieldChange(
            FeatureToggleField field,
            Object before,
            Object after
    ) {

        static FeatureToggleFieldChange fieldChange(FeatureToggleField field, Object before, Object after) {
            return new FeatureToggleFieldChange(field, before, after);
        }

    }

    static class ChangeSet {
        private final List<FeatureToggleFieldChange> changes = new ArrayList<>();

        static ChangeSet createChangeSet() {
            return new ChangeSet();
        }

        void addIfChanged(FeatureToggleField field, Object before, Object after) {
            if (!Objects.equals(before, after)) {
                changes.add(fieldChange(field, before, after));
            }
        }

        public boolean isEmpty() {
            return changes.isEmpty();
        }

        public FeatureToggleFieldChange[] toArray() {
            return changes.toArray(FeatureToggleFieldChange[]::new);
        }
    }
}