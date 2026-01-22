package pl.feature.toggle.service.write.domain.featuretoggle;

import java.util.List;

public record FeatureToggleUpdateResult(
        FeatureToggle updated,
        List<FeatureToggleFieldChange> changes
) {

    public boolean hasChanges() {
        return !changes.isEmpty();
    }

    public record FeatureToggleFieldChange(
            FeatureToggleField field,
            Object before,
            Object after
    ) {

        static FeatureToggleFieldChange change(FeatureToggleField field, Object before, Object after) {
            return new FeatureToggleFieldChange(field, before, after);
        }

    }

}
