package pl.feature.toggle.service.write.builder;

import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.value.FeatureToggleValue;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;

public class FakeFeatureToggleBuilder {
    private FeatureToggleId id;
    private EnvironmentId environmentId;
    private FeatureToggleName name;
    private FeatureToggleDescription description;
    private FeatureToggleValue value;
    private FeatureToggleStatus status;
    private CreatedAt createdAt;
    private UpdatedAt updatedAt;
    private Revision revision;

    private FakeFeatureToggleBuilder() {
        id = FeatureToggleId.create();
        environmentId = EnvironmentId.create();
        name = FeatureToggleName.create("TEST");
        description = FeatureToggleDescription.create("TEST");
        value = FeatureToggleValueBuilder.bool(true);
        status = FeatureToggleStatus.ACTIVE;
        createdAt = CreatedAt.now();
        updatedAt = UpdatedAt.now();
        revision = Revision.initialRevision();
    }

    public static FakeFeatureToggleBuilder fakeFeatureToggleBuilder() {
        return new FakeFeatureToggleBuilder();
    }

    public FakeFeatureToggleBuilder id(FeatureToggleId id) {
        this.id = id;
        return this;
    }

    public FakeFeatureToggleBuilder environmentId(EnvironmentId environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public FakeFeatureToggleBuilder name(FeatureToggleName name) {
        this.name = name;
        return this;
    }

    public FakeFeatureToggleBuilder description(FeatureToggleDescription description) {
        this.description = description;
        return this;
    }

    public FakeFeatureToggleBuilder value(FeatureToggleValue value) {
        this.value = value;
        return this;
    }

    public FakeFeatureToggleBuilder status(FeatureToggleStatus status) {
        this.status = status;
        return this;
    }

    public FakeFeatureToggleBuilder createdAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public FakeFeatureToggleBuilder updatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public FakeFeatureToggleBuilder revision(Revision revision) {
        this.revision = revision;
        return this;
    }

    public FeatureToggle build() {
        return new FeatureToggle(id, environmentId, name, description, value, status, createdAt, updatedAt, revision);
    }
}
