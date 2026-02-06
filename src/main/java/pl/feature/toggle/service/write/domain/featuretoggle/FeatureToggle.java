package pl.feature.toggle.service.write.domain.featuretoggle;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.value.FeatureToggleValue;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.value.raw.FeatureToggleRawValue;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.CannotOperateOnArchivedFeatureToggleException;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleField.*;
import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleStatus.ACTIVE;
import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleStatus.ARCHIVED;
import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult.ChangeSet.createChangeSet;
import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult.FeatureToggleFieldChange.fieldChange;
import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult.noChanges;
import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult.updated;

public record FeatureToggle(
        FeatureToggleId id,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        FeatureToggleStatus status,
        CreatedAt createdAt,
        UpdatedAt updatedAt,
        Revision revision
) {


    public static FeatureToggle create(CreateFeatureToggleCommand command, EnvironmentRef environmentRef) {
        return new FeatureToggle(
                FeatureToggleId.create(),
                environmentRef.environmentId(),
                command.name(),
                command.description(),
                FeatureToggleValueBuilder.from(command.rawValue(), command.valueType()),
                ACTIVE,
                CreatedAt.now(),
                UpdatedAt.now(),
                Revision.initialRevision()
        );
    }

    public FeatureToggleUpdateResult archive() {
        if (isArchived()) {
            return noChanges(this);
        }
        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                name,
                description,
                value,
                ARCHIVED,
                createdAt,
                UpdatedAt.now(),
                revision.next());
        var fieldChange = fieldChange(STATUS, ACTIVE, ARCHIVED);
        return updated(featureToggle, revision, fieldChange);
    }

    public FeatureToggleUpdateResult restore() {
        if (isActive()) {
            return noChanges(this);
        }
        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                name,
                description,
                value,
                ACTIVE,
                createdAt,
                UpdatedAt.now(),
                revision.next());
        var fieldChange = fieldChange(STATUS, ARCHIVED, ACTIVE);
        return updated(featureToggle, revision, fieldChange);
    }

    public FeatureToggleUpdateResult changeValue(FeatureToggleRawValue newValue) {
        if (isArchived()) {
            throw new CannotOperateOnArchivedFeatureToggleException(name);
        }
        var newFeatureToggleValue = FeatureToggleValueBuilder.from(newValue, value.type());

        var changeSet = createChangeSet();
        changeSet.addIfChanged(VALUE, value, newFeatureToggleValue);

        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                name,
                description,
                newFeatureToggleValue,
                status,
                createdAt,
                UpdatedAt.now(),
                revision.next());
        return updated(featureToggle, revision, changeSet.toArray());
    }

    public FeatureToggleUpdateResult update(FeatureToggleName newName, FeatureToggleDescription newDescription) {
        if (isArchived()) {
            throw new CannotOperateOnArchivedFeatureToggleException(name);
        }
        var changeSet = createChangeSet();
        changeSet.addIfChanged(NAME, name, newName);
        changeSet.addIfChanged(DESCRIPTION, description, newDescription);

        if (changeSet.isEmpty()) {
            return noChanges(this);
        }

        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                newName,
                newDescription,
                value,
                status,
                createdAt,
                UpdatedAt.now(),
                revision.next());
        return updated(featureToggle, revision, changeSet.toArray());
    }

    public boolean isArchived() {
        return ARCHIVED == status;
    }

    public boolean isActive() {
        return FeatureToggleStatus.ACTIVE == status;
    }


}
