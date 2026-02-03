package pl.feature.toggle.service.write.domain.featuretoggle;

import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult.FeatureToggleFieldChange.fieldChange;

public record FeatureToggleUpdateResult(
        FeatureToggle featureToggle,
        List<FeatureToggleFieldChange> changes
) {

    public static FeatureToggleUpdateResult updated(FeatureToggle featureToggle, FeatureToggleFieldChange... changes) {
        return new FeatureToggleUpdateResult(featureToggle, List.of(changes));
    }

    public static FeatureToggleUpdateResult noChanges(FeatureToggle featureToggle) {
        return new FeatureToggleUpdateResult(featureToggle, List.of());
    }

    public static FeatureToggleUpdateResult of(FeatureToggle featureToggle, List<FeatureToggleFieldChange> changes) {
        return new FeatureToggleUpdateResult(featureToggle, changes);
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