package pl.feature.toggle.service.domain.featuretoggle;

import com.ftaas.domain.featuretoggle.FeatureToggleType;
import com.ftaas.domain.CreatedAt;
import com.ftaas.domain.UpdatedAt;
import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.featuretoggle.FeatureToggleDescription;
import com.ftaas.domain.featuretoggle.FeatureToggleId;
import com.ftaas.domain.featuretoggle.FeatureToggleName;
import com.ftaas.domain.featuretoggle.FeatureToggleValue;
import com.ftaas.domain.project.ProjectId;
import pl.feature.toggle.service.application.port.in.command.CreateFeatureToggleCommand;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;

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
