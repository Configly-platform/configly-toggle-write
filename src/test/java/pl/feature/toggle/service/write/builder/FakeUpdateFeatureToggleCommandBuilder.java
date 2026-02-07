package pl.feature.toggle.service.write.builder;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.command.UpdateFeatureToggleCommand;

import java.util.UUID;

public class FakeUpdateFeatureToggleCommandBuilder {

    private FeatureToggleId featureToggleId;
    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleName name;
    private FeatureToggleDescription description;

    private FakeUpdateFeatureToggleCommandBuilder() {
        this.featureToggleId = FeatureToggleId.create();
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.name = FeatureToggleName.create("name");
        this.description = FeatureToggleDescription.create("description");
    }

    public static FakeUpdateFeatureToggleCommandBuilder fakeUpdateFeatureToggleCommandBuilder() {
        return new FakeUpdateFeatureToggleCommandBuilder();
    }

    public FakeUpdateFeatureToggleCommandBuilder withProjectId(String projectId) {
        this.projectId = ProjectId.create(projectId);
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withFeatureToggleId(String featureToggleId) {
        this.featureToggleId = FeatureToggleId.create(featureToggleId);
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withEnvironmentId(String environmentId) {
        this.environmentId = EnvironmentId.create(environmentId);
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withEnvironmentId(EnvironmentId environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withProjectId(ProjectId projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withName(String name) {
        this.name = FeatureToggleName.create(name);
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withName(FeatureToggleName name) {
        this.name = name;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withDescription(FeatureToggleDescription description) {
        this.description = description;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withDescription(String description) {
        this.description = FeatureToggleDescription.create(description);
        return this;
    }

    public UpdateFeatureToggleCommand build() {
        return new UpdateFeatureToggleCommand(featureToggleId, projectId, environmentId, name, description);
    }

}
