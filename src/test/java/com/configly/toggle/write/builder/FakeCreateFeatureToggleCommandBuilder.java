package com.configly.toggle.write.builder;

import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.project.ProjectId;
import com.configly.web.actor.Actor;
import com.configly.web.correlation.CorrelationId;
import com.configly.value.FeatureToggleValueSnapshot;
import com.configly.value.FeatureToggleValueType;
import com.configly.toggle.write.application.port.in.command.CreateFeatureToggleCommand;

public class FakeCreateFeatureToggleCommandBuilder {

    private ProjectId projectId;
    private EnvironmentId environmentId;
    private FeatureToggleName name;
    private FeatureToggleDescription description;
    private FeatureToggleValueType type;
    private FeatureToggleValueSnapshot value;
    private Actor actor;
    private CorrelationId correlationId;

    private FakeCreateFeatureToggleCommandBuilder() {
        this.projectId = ProjectId.create();
        this.environmentId = EnvironmentId.create();
        this.name = FeatureToggleName.create("name");
        this.description = FeatureToggleDescription.create("description");
        this.type = FeatureToggleValueType.BOOLEAN;
        this.value = FeatureToggleValueSnapshot.of(Boolean.TRUE.toString());
        this.actor = Actor.system();
        this.correlationId = CorrelationId.generate();
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

    public FakeCreateFeatureToggleCommandBuilder withActor(Actor actor) {
        this.actor = actor;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withCorrelationId(String correlationId) {
        this.correlationId = CorrelationId.of(correlationId);
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
        return new CreateFeatureToggleCommand(projectId, environmentId, name, description, value, type, actor, correlationId);
    }

}
