package pl.feature.toggle.service.write.builder;

import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.in.command.ChangeFeatureToggleStatusCommand;

public class FakeChangeFeatureToggleStatusCommandBuilder {

    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleId featureToggleId;
    private FeatureToggleStatus newStatus;

    private FakeChangeFeatureToggleStatusCommandBuilder() {
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.featureToggleId = FeatureToggleId.create();
        this.newStatus = FeatureToggleStatus.ACTIVE;
    }

    public static FakeChangeFeatureToggleStatusCommandBuilder fakeChangeFeatureToggleStatusCommandBuilder() {
        return new FakeChangeFeatureToggleStatusCommandBuilder();
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withProjectId(String projectId) {
        this.projectId = ProjectId.create(projectId);
        return this;
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withEnvironmentId(String environmentId) {
        this.environmentId = EnvironmentId.create(environmentId);
        return this;
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withFeatureToggleId(String featureToggleId) {
        this.featureToggleId = FeatureToggleId.create(featureToggleId);
        return this;
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withProjectId(ProjectId projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withEnvironmentId(EnvironmentId environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withFeatureToggleId(FeatureToggleId featureToggleId) {
        this.featureToggleId = featureToggleId;
        return this;
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withNewStatus(FeatureToggleStatus newStatus) {
        this.newStatus = newStatus;
        return this;
    }

    public ChangeFeatureToggleStatusCommand build() {
        return new ChangeFeatureToggleStatusCommand(projectId, environmentId, featureToggleId, newStatus);
    }
}
