package com.configly.toggle.write.builder;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.project.ProjectId;
import com.configly.web.actor.Actor;
import com.configly.web.correlation.CorrelationId;
import com.configly.toggle.write.application.port.in.command.UpdateFeatureToggleCommand;

public class FakeUpdateFeatureToggleCommandBuilder {

    private FeatureToggleId featureToggleId;
    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleName name;
    private FeatureToggleDescription description;
    private Actor actor;
    private CorrelationId correlationId;

    private FakeUpdateFeatureToggleCommandBuilder() {
        this.featureToggleId = FeatureToggleId.create();
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.name = FeatureToggleName.create("name");
        this.description = FeatureToggleDescription.create("description");
        this.actor = Actor.system();
        this.correlationId = CorrelationId.generate();
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

    public FakeUpdateFeatureToggleCommandBuilder withActor(Actor actor) {
        this.actor = actor;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withCorrelationId(String correlationId) {
        this.correlationId = CorrelationId.of(correlationId);
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
        return new UpdateFeatureToggleCommand(featureToggleId, projectId, environmentId, name, description, actor, correlationId);
    }

}
