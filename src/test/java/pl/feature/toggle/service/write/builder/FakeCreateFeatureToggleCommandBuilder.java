package pl.feature.toggle.service.write.builder;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.value.FeatureToggleValueType;
import pl.feature.toggle.service.value.FeatureToggleValueSnapshot;
import pl.feature.toggle.service.write.application.port.in.command.CreateFeatureToggleCommand;

public class FakeCreateFeatureToggleCommandBuilder {

    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleName name;
    private FeatureToggleDescription description;
    private FeatureToggleValueType type;
    private FeatureToggleValueSnapshot value;

    private FakeCreateFeatureToggleCommandBuilder() {
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.name = FeatureToggleName.create("name");
        this.description = FeatureToggleDescription.create("description");
        this.type = FeatureToggleValueType.BOOLEAN;
        this.value = FeatureToggleValueSnapshot.of(Boolean.TRUE.toString());
    }

    public static FakeCreateFeatureToggleCommandBuilder fakeCreateFeatureToggleCommandBuilder() {
        return new FakeCreateFeatureToggleCommandBuilder();
    }

    public FakeCreateFeatureToggleCommandBuilder withProjectId(String projectId) {
        this.projectId = ProjectId.create(projectId);
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withEnvironmentId(String environmentId) {
        this.environmentId = EnvironmentId.create(environmentId);
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withEnvironmentId(EnvironmentId environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withProjectId(ProjectId projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withName(String name) {
        this.name = FeatureToggleName.create(name);
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withDescription(String description) {
        this.description = FeatureToggleDescription.create(description);
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withType(FeatureToggleValueType type) {
        this.type = type;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withValue(String value) {
        this.value = FeatureToggleValueSnapshot.of(value);
        return this;
    }

    public CreateFeatureToggleCommand build() {
        return new CreateFeatureToggleCommand(projectId, environmentId, name, description, value, type);
    }

}
