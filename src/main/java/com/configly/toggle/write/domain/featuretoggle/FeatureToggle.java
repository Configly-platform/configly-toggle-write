package com.configly.toggle.write.domain.featuretoggle;

import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleRulesCommand;
import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleRulesCommand.RuleCommand;
import com.configly.value.toggle.FeatureToggleValue;
import com.configly.value.toggle.FeatureToggleValueBuilder;
import com.configly.value.toggle.FeatureToggleValueSnapshot;
import com.configly.toggle.write.application.port.in.command.CreateFeatureToggleCommand;
import com.configly.toggle.write.domain.featuretoggle.exception.CannotOperateOnArchivedFeatureToggleException;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

import java.util.List;

import static com.configly.model.featuretoggle.FeatureToggleStatus.ACTIVE;
import static com.configly.model.featuretoggle.FeatureToggleStatus.ARCHIVED;
import static com.configly.toggle.write.domain.featuretoggle.FeatureToggleField.*;
import static com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult.ChangeSet.createChangeSet;
import static com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult.FeatureToggleFieldChange.fieldChange;
import static com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult.noChanges;
import static com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult.updated;

public record FeatureToggle(
        FeatureToggleId id,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        FeatureToggleStatus status,
        CreatedAt createdAt,
        UpdatedAt updatedAt,
        Revision revision,
        Rules rules
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
                Revision.initialRevision(),
                Rules.empty()
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
                revision.next(),
                rules);
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
                revision.next(),
                rules);
        var fieldChange = fieldChange(STATUS, ARCHIVED, ACTIVE);
        return updated(featureToggle, revision, fieldChange);
    }

    public FeatureToggleUpdateResult changeValue(FeatureToggleValueSnapshot newValue) {
        if (isArchived()) {
            throw new CannotOperateOnArchivedFeatureToggleException(name);
        }
        var newFeatureToggleValue = FeatureToggleValueBuilder.from(newValue, value.type());

        var changeSet = createChangeSet();
        changeSet.addIfChanged(VALUE, value, newFeatureToggleValue);

        if (changeSet.isEmpty()) {
            return noChanges(this);
        }

        var featureToggle = new FeatureToggle(
                id,
                environmentId,
                name,
                description,
                newFeatureToggleValue,
                status,
                createdAt,
                UpdatedAt.now(),
                revision.next(),
                rules);
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
                revision.next(),
                rules);
        return updated(featureToggle, revision, changeSet.toArray());
    }

    public boolean isArchived() {
        return ARCHIVED == status;
    }

    public boolean isActive() {
        return ACTIVE == status;
    }


    public void changeRules(List<RuleCommand> rulesCommands) {
        if (isArchived()){
            throw new CannotOperateOnArchivedFeatureToggleException(name);
        }
        // TODO

    }
}
