package pl.feature.toggle.service.write.domain.featuretoggle;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValue;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.CannotOperateOnArchivedFeatureToggleException;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

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
        ProjectId projectId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        FeatureToggleStatus status,
        CreatedAt createdAt,
        UpdatedAt updatedAt
) {


    public static FeatureToggle create(CreateFeatureToggleCommand command, ProjectRef projectRef, EnvironmentRef environmentRef) {
        return new FeatureToggle(
                FeatureToggleId.create(),
                environmentRef.environmentId(),
                projectRef.projectId(),
                command.name(),
                command.description(),
                command.value(),
                ACTIVE,
                CreatedAt.now(),
                UpdatedAt.now()
        );
    }

    public FeatureToggleUpdateResult archive() {
        if (isArchived()) {
            return noChanges(this);
        }
        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                projectId,
                name,
                description,
                value,
                ARCHIVED,
                createdAt,
                UpdatedAt.now());
        var fieldChange = fieldChange(STATUS, ACTIVE, ARCHIVED);
        return updated(featureToggle, fieldChange);
    }

    public FeatureToggleUpdateResult restore() {
        if (isActive()) {
            return noChanges(this);
        }
        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                projectId,
                name,
                description,
                value,
                ACTIVE,
                createdAt,
                UpdatedAt.now());
        var fieldChange = fieldChange(STATUS, ARCHIVED, ACTIVE);
        return updated(featureToggle, fieldChange);
    }

    public FeatureToggleUpdateResult changeValue(FeatureToggleValue newValue) {
        if (isArchived()) {
            throw new CannotOperateOnArchivedFeatureToggleException(name);
        }
        var changeSet = createChangeSet();
        changeSet.addIfChanged(FeatureToggleField.VALUE, value, newValue);
        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                projectId,
                name,
                description,
                value,
                status,
                createdAt,
                UpdatedAt.now());
        return updated(featureToggle, changeSet.toArray());
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
                projectId,
                newName,
                newDescription,
                value,
                status,
                createdAt,
                UpdatedAt.now());
        return updated(featureToggle, changeSet.toArray());
    }

    public boolean isArchived() {
        return ARCHIVED == status;
    }

    public boolean isActive() {
        return FeatureToggleStatus.ACTIVE == status;
    }


}
