package com.configly.toggle.write.builder;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.project.ProjectId;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.value.FeatureToggleValueSnapshot;
import com.configly.toggle.write.application.port.in.command.ChangeFeatureToggleValueCommand;

public class FakeChangeFeatureToggleValueCommandBuilder {
    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleId featureToggleId;
    private FeatureToggleValueSnapshot newValue;
    private Actor actor;
    private CorrelationId correlationId;

    private FakeChangeFeatureToggleValueCommandBuilder() {
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.featureToggleId = FeatureToggleId.create();
        this.newValue = FeatureToggleValueSnapshot.of("TRUE");
        this.actor = Actor.system();
        this.correlationId = CorrelationId.generate();
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

    public FakeChangeFeatureToggleValueCommandBuilder withActor(Actor actor) {
        this.actor = actor;
        return this;
    }

    public FakeChangeFeatureToggleValueCommandBuilder withCorrelationId(String correlationId) {
        this.correlationId = CorrelationId.of(correlationId);
        return this;
    }

    public FakeChangeFeatureToggleValueCommandBuilder withNewValue(String newValue) {
        this.newValue = FeatureToggleValueSnapshot.of(newValue);
        return this;
    }

    public ChangeFeatureToggleValueCommand build() {
        return new ChangeFeatureToggleValueCommand(projectId, environmentId, featureToggleId, newValue, actor, correlationId);
    }
}
