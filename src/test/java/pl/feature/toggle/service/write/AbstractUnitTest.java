package pl.feature.toggle.service.write;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.internal.DefaultRevisionProjectionApplier;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.outbox.FakeOutboxWriter;
import pl.feature.toggle.service.write.application.policy.FeatureTogglePolicyFacade;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.infrastructure.support.*;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static pl.feature.toggle.service.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

public abstract class AbstractUnitTest {

    protected static final ProjectRef ACTIVE_PROJECT = fakeProjectRefBuilder()
            .status(ProjectStatus.ACTIVE)
            .build();

    protected static final ProjectRef ARCHIVED_PROJECT = fakeProjectRefBuilder()
            .status(ProjectStatus.ARCHIVED)
            .build();

    protected static final EnvironmentRef ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT = fakeEnvironmentRefBuilder()
            .status(EnvironmentStatus.ACTIVE)
            .projectId(ACTIVE_PROJECT.projectId())
            .build();

    protected static final EnvironmentRef ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT = fakeEnvironmentRefBuilder()
            .status(EnvironmentStatus.ARCHIVED)
            .projectId(ARCHIVED_PROJECT.projectId())
            .build();

    protected static final EnvironmentRef ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT = fakeEnvironmentRefBuilder()
            .status(EnvironmentStatus.ARCHIVED)
            .projectId(ACTIVE_PROJECT.projectId())
            .build();

    protected static final FeatureToggle ACTIVE_FEATURE_TOGGLE = fakeFeatureToggleBuilder()
            .status(FeatureToggleStatus.ACTIVE)
            .build();

    protected static final FeatureToggle ARCHIVED_FEATURE_TOGGLE = fakeFeatureToggleBuilder()
            .status(FeatureToggleStatus.ARCHIVED)
            .build();

    protected ConfigurationClientStub configurationClientStub;
    protected EnvironmentRefProjectionRepositorySpy environmentRefRepositorySpy;
    protected FeatureToggleCommandRepositorySpy featureToggleCommandRepositorySpy;
    protected FeatureToggleQueryRepositoryStub featureToggleQueryRepositoryStub;
    protected ProjectRefProjectionRepositorySpy projectRefRepositorySpy;
    protected FakeInMemoryProjectRefRepository fakeInMemoryProjectRefRepository;
    protected ProjectRefQueryRepositoryStub projectRefQueryRepositoryStub;
    protected EnvironmentRefQueryRepositoryStub environmentRefQueryRepositoryStub;
    protected FakeOutboxWriter outboxWriter;
    protected FakeCorrelationProvider correlationProvider;
    protected FakeActorProvider actorProvider;
    protected FakeAcknowledgment acknowledgment;
    protected FeatureTogglePolicyFacade featureTogglePolicyFacade;
    protected ProjectRefConsistencySpy projectRefConsistencySpy;
    protected EnvironmentRefConsistencySpy environmentRefConsistencySpy;
    protected ApplicationEventPublishedSpy applicationEventPublishedSpy;
    protected RevisionProjectionApplier revisionProjectionApplier;

    @BeforeEach
    void setUp() {
        outboxWriter = new FakeOutboxWriter();
        actorProvider = new FakeActorProvider();
        correlationProvider = new FakeCorrelationProvider();
        acknowledgment = new FakeAcknowledgment();
        configurationClientStub = new ConfigurationClientStub();
        environmentRefRepositorySpy = new EnvironmentRefProjectionRepositorySpy();
        featureToggleCommandRepositorySpy = new FeatureToggleCommandRepositorySpy();
        featureToggleQueryRepositoryStub = new FeatureToggleQueryRepositoryStub();
        projectRefRepositorySpy = new ProjectRefProjectionRepositorySpy();
        fakeInMemoryProjectRefRepository = new FakeInMemoryProjectRefRepository();
        featureTogglePolicyFacade = FeatureTogglePolicyFacade.create(featureToggleQueryRepositoryStub);
        projectRefConsistencySpy = new ProjectRefConsistencySpy();
        applicationEventPublishedSpy = new ApplicationEventPublishedSpy();
        environmentRefConsistencySpy = new EnvironmentRefConsistencySpy();
        projectRefQueryRepositoryStub = new ProjectRefQueryRepositoryStub();
        environmentRefQueryRepositoryStub = new EnvironmentRefQueryRepositoryStub();
        revisionProjectionApplier = DefaultRevisionProjectionApplier.create();
    }

    @AfterEach
    void tearDown() {
        configurationClientStub.reset();
        environmentRefRepositorySpy.reset();
        featureToggleCommandRepositorySpy.reset();
        featureToggleQueryRepositoryStub.reset();
        projectRefRepositorySpy.reset();
        fakeInMemoryProjectRefRepository.reset();
        projectRefQueryRepositoryStub.reset();
        environmentRefQueryRepositoryStub.reset();
        applicationEventPublishedSpy.reset();
    }

    protected void assertContainsEventOfType(String topic, Class<?> eventClass) {
        assertThat(outboxWriter.containsEventOfType(topic, eventClass)).isTrue();
    }

    protected void assertNoEventsHasBeenPublished() {
        assertThat(outboxWriter.noEventsHaveBeenPublished()).isTrue();
    }

    protected <T extends IntegrationEvent> T getLastPublishedEvent(String topic, Class<T> eventClass) {
        return outboxWriter.lastEventOfType(topic, eventClass);
    }

    protected void assertDoesNotContainEventOfType(String topic, Class<?> eventClass) {
        assertThat(outboxWriter.containsEventOfType(topic, eventClass)).isFalse();
    }

    protected void assertHasEventCountOfType(String topic, Class<?> eventClass, int eventCount) {
        assertThat(outboxWriter.hasEventTypeCountForTopic(topic, eventClass, eventCount)).isTrue();
    }


}
