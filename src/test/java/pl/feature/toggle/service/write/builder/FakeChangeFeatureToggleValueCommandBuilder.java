package pl.feature.toggle.service.write.builder;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.value.FeatureToggleValueSnapshot;
import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleValueCommand;

public class FakeChangeFeatureToggleValueCommandBuilder {
    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleId featureToggleId;
    private FeatureToggleValueSnapshot newValue;

    private FakeChangeFeatureToggleValueCommandBuilder() {
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.featureToggleId = FeatureToggleId.create();
        this.newValue = FeatureToggleValueSnapshot.of("TRUE");
    }

    public static FakeChangeFeatureToggleValueCommandBuilder fakeChangeFeatureToggleValueCommandBuilder() {
        return new FakeChangeFeatureToggleValueCommandBuilder();
    }

    public FakeChangeFeatureToggleValueCommandBuilder withProjectId(String projectId) {
        this.projectId = ProjectId.create(projectId);
        return this;
    }

    public FakeChangeFeatureToggleValueCommandBuilder withEnvironmentId(String environmentId) {
        this.environmentId = EnvironmentId.create(environmentId);
        return this;
    }

    public FakeChangeFeatureToggleValueCommandBuilder withFeatureToggleId(String featureToggleId) {
        this.featureToggleId = FeatureToggleId.create(featureToggleId);
        return this;
    }

    public FakeChangeFeatureToggleValueCommandBuilder withNewValue(String newValue) {
        this.newValue = FeatureToggleValueSnapshot.of(newValue);
        return this;
    }

    public ChangeFeatureToggleValueCommand build() {
        return new ChangeFeatureToggleValueCommand(projectId, environmentId, featureToggleId, newValue);
    }
}
