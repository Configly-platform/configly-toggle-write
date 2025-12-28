package pl.feature.toggle.service.write.domain.featuretoggle;

import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValue;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.*;
import pl.feature.toggle.service.model.project.ProjectId;

public record FeatureToggle(
        FeatureToggleId id,
        EnvironmentId environmentId,
        ProjectId projectId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleType type,
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
                command.type(),
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
            FeatureToggleType type,
            FeatureToggleValue value
    ) {
        return new FeatureToggle(
                FeatureToggleId.create(),
                environmentId,
                projectId,
                name,
                description,
                type,
                value,
                CreatedAt.now(),
                UpdatedAt.now()
        );
    }

    public FeatureToggle update(ProjectId projectId,
                                EnvironmentId environmentId,
                                FeatureToggleName name,
                                FeatureToggleDescription description,
                                FeatureToggleType type,
                                FeatureToggleValue value
    ) {
        return this;
    }

}
