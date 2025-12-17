package pl.feature.toggle.service;

import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.infrastructure.FakeEnvironmentRepository;
import pl.feature.toggle.service.infrastructure.FakeFeatureToggleRepository;
import pl.feature.toggle.service.infrastructure.FakeProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleType;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.outbox.FakeOutboxWriter;

import static pl.feature.toggle.service.builder.FakeCreateFeatureToggleCommandBuilder.createFeatureToggleCommandBuilder;

public abstract class AbstractUnitTest {

    protected FakeFeatureToggleRepository featureToggleRepository;
    protected FakeEnvironmentRepository environmentRepository;
    protected FakeProjectRepository projectRepository;
    protected FakeOutboxWriter outboxWriter;

    @BeforeEach
    void setUp() {
        featureToggleRepository = new FakeFeatureToggleRepository();
        environmentRepository = new FakeEnvironmentRepository();
        projectRepository = new FakeProjectRepository();
        outboxWriter = new FakeOutboxWriter();
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
                .withType(FeatureToggleType.BOOLEAN)
                .withValue("true")
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
