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
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult.FeatureToggleFieldChange;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult.FeatureToggleFieldChange.change;

public record FeatureToggle(
        FeatureToggleId id,
        EnvironmentId environmentId,
        ProjectId projectId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        CreatedAt createdAt,
        UpdatedAt updatedAt
) {


    public static FeatureToggle create(CreateFeatureToggleCommand command, ProjectSnapshot project, EnvironmentSnapshot environment) {
        environment.mustBelongTo(project.id());
        return new FeatureToggle(
                FeatureToggleId.create(),
                command.environmentId(),
                project.id(),
                command.name(),
                command.description(),
                command.value(),
                CreatedAt.now(),
                UpdatedAt.now()
        );
    }

    public static FeatureToggle create(
            EnvironmentId environmentId,
            ProjectId projectId,
            FeatureToggleName name,
            FeatureToggleDescription description,
            FeatureToggleValue value
    ) {
        return new FeatureToggle(
                FeatureToggleId.create(),
                environmentId,
                projectId,
                name,
                description,
                value,
                CreatedAt.now(),
                UpdatedAt.now()
        );
    }

    public FeatureToggleUpdateResult update(ProjectId projectId,
                                            EnvironmentId environmentId,
                                            FeatureToggleName name,
                                            FeatureToggleDescription description,
                                            FeatureToggleValue value
    ) {

        List<FeatureToggleFieldChange> changes = new ArrayList<>();
        // TODO - docelowo tutaj tylko update nazwy/opisu i tych prostych pol - reszta inne wejscia domenowe

        if (!Objects.equals(this.name, name)) {
            changes.add(change(FeatureToggleField.NAME, this.name, name));
        }

        if (!Objects.equals(this.environmentId, environmentId)) {
            changes.add(change(FeatureToggleField.ENVIRONMENT_ID, this.environmentId, environmentId));
        }

        if (!Objects.equals(this.projectId, projectId)) {
            changes.add(change(FeatureToggleField.PROJECT_ID, this.projectId, projectId));
        }

        if (!Objects.equals(this.description, description)) {
            changes.add(change(FeatureToggleField.DESCRIPTION, this.description, description));
        }

        if (!Objects.equals(this.value, value)) {
            changes.add(change(FeatureToggleField.VALUE, this.value, value));
        }

        FeatureToggle updated = new FeatureToggle(
                this.id,
                environmentId,
                projectId,
                name,
                description,
                value,
                this.createdAt,
                UpdatedAt.now()
        );

        return new FeatureToggleUpdateResult(updated, List.copyOf(changes));
    }

}
