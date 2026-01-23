package pl.feature.toggle.service.write.builder;

import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.write.application.port.in.command.UpdateFeatureToggleCommand;

import java.util.UUID;

public class FakeUpdateFeatureToggleCommandBuilder {

    private String featureToggleId;
    private String projectId;
    private String environmentId;
    private String name;
    private String description;
    private Object value;

    private FakeUpdateFeatureToggleCommandBuilder() {
        this.featureToggleId = UUID.randomUUID().toString();
        this.projectId = UUID.randomUUID().toString();
        this.environmentId = UUID.randomUUID().toString();
        this.name = "name";
        this.description = "description";
        this.value = true;
    }

    public static FakeUpdateFeatureToggleCommandBuilder createFeatureToggleCommandBuilder() {
        return new FakeUpdateFeatureToggleCommandBuilder();
    }

    public FakeUpdateFeatureToggleCommandBuilder withProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withFeatureToggleId(String featureToggleId) {
        this.featureToggleId = featureToggleId;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public FakeUpdateFeatureToggleCommandBuilder withValue(Object value) {
        this.value = value;
        return this;
    }

    public UpdateFeatureToggleCommand build() {
        return UpdateFeatureToggleCommand.from(UUID.fromString(featureToggleId), UUID.fromString(projectId),
                UUID.fromString(environmentId), name, description, value);
    }

}
