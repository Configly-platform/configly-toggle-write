package pl.feature.toggle.service.write;

import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueType;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.write.infrastructure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.outbox.FakeOutboxWriter;

import static pl.feature.toggle.service.write.builder.FakeCreateFeatureToggleCommandBuilder.createFeatureToggleCommandBuilder;

public abstract class AbstractUnitTest {

    protected FakeFeatureToggleQueryRepository featureToggleRepository;
    protected FakeEnvironmentRefRepository environmentRepository;
    protected FakeProjectRefRepository projectRepository;
    protected FakeOutboxWriter outboxWriter;
    protected FakeCorrelationProvider correlationProvider;
    protected FakeActorProvider actorProvider;
    protected FakeAcknowledgment acknowledgment;

    @BeforeEach
    void setUp() {
        featureToggleRepository = new FakeFeatureToggleQueryRepository();
        environmentRepository = new FakeEnvironmentRefRepository();
        projectRepository = new FakeProjectRefRepository();
        outboxWriter = new FakeOutboxWriter();
        actorProvider = new FakeActorProvider();
        correlationProvider = new FakeCorrelationProvider();
        acknowledgment = new FakeAcknowledgment();
    }

    @AfterEach
    void tearDown() {
        featureToggleRepository.clear();
        environmentRepository.clear();
        projectRepository.clear();
    }

    protected ProjectSnapshot createProject() {
        var projectSnapshot = ProjectSnapshot.create();
        projectRepository.save(projectSnapshot);
        return projectSnapshot;
    }

    protected FeatureToggle createFeatureToggle(String name, ProjectId projectId, EnvironmentId environmentId) {
        var command = createFeatureToggleCommandBuilder()
                .withDescription("TEST")
                .withName(name)
                .withType(FeatureToggleValueType.BOOLEAN)
                .withValue("TRUE")
                .withEnvironmentId(environmentId.idAsString())
                .withProjectId(projectId.idAsString())
                .build();
        var featureToggle = FeatureToggle.create(command, new ProjectSnapshot(projectId), new EnvironmentSnapshot(environmentId, projectId));
        featureToggleRepository.save(featureToggle);
        return featureToggle;
    }

    protected EnvironmentSnapshot createEnvironment(ProjectId projectId) {
        var environmentSnapshot = EnvironmentSnapshot.create(projectId);
        environmentRepository.save(environmentSnapshot);
        return environmentSnapshot;
    }
}
