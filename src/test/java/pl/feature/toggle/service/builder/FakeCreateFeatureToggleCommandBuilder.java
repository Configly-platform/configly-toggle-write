package pl.feature.toggle.service.builder;

import com.ftaas.domain.featuretoggle.FeatureToggleType;
import pl.feature.toggle.service.application.port.in.command.CreateFeatureToggleCommand;

import java.util.UUID;

public class FakeCreateFeatureToggleCommandBuilder {

    private String projectId;
    private String environmentId;
    private String name;
    private String description;
    private FeatureToggleType type;
    private String value;

    private FakeCreateFeatureToggleCommandBuilder() {
        this.projectId = UUID.randomUUID().toString();
        this.environmentId = UUID.randomUUID().toString();
        this.name = "name";
        this.description = "description";
        this.type = FeatureToggleType.BOOLEAN;
        this.value = "true";
    }

    public static FakeCreateFeatureToggleCommandBuilder createFeatureToggleCommandBuilder() {
        return new FakeCreateFeatureToggleCommandBuilder();
    }

    public FakeCreateFeatureToggleCommandBuilder withProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withType(FeatureToggleType type) {
        this.type = type;
        return this;
    }

    public FakeCreateFeatureToggleCommandBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public CreateFeatureToggleCommand build() {
        return CreateFeatureToggleCommand.from(UUID.fromString(projectId), UUID.fromString(environmentId), name, description, type, value);
    }

}
