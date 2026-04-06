package com.configly.toggle.write.builder;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.model.project.ProjectId;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleStatusCommand;

public class FakeChangeFeatureToggleStatusCommandBuilder {

    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleId featureToggleId;
    private FeatureToggleStatus newStatus;
    private Actor actor;
    private CorrelationId correlationId;

    private FakeChangeFeatureToggleStatusCommandBuilder() {
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.featureToggleId = FeatureToggleId.create();
        this.newStatus = FeatureToggleStatus.ACTIVE;
        this.actor = Actor.system();
        this.correlationId = CorrelationId.generate();
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

    public FakeChangeFeatureToggleStatusCommandBuilder withActor(Actor actor) {
        this.actor = actor;
        return this;
    }

    public FakeChangeFeatureToggleStatusCommandBuilder withCorrelationId(String correlationId) {
        this.correlationId = CorrelationId.of(correlationId);
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
        return new ChangeFeatureToggleStatusCommand(projectId, environmentId, featureToggleId, newStatus, actor, correlationId);
    }
}
