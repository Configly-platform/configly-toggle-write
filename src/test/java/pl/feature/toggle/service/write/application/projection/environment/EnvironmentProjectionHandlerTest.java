package pl.feature.toggle.service.write.application.projection.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.projection.environment.event.RebuildEnvironmentRefRequested;
import pl.feature.toggle.service.write.domain.reference.EnvironmentStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged.environmentStatusChangedEventBuilder;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;

class EnvironmentProjectionHandlerTest extends AbstractUnitTest {

    private EnvironmentProjection sut;

    @BeforeEach
    void setUp() {
        sut = EnvironmentProjectionFacade.environmentProjection(
                environmentRefRepositoryStubSpy,
                environmentRefQueryRepositoryStub,
                revisionProjectionApplier,
                applicationEventPublishedSpy
        );
    }

    @Test
    void should_insert_new_environment_when_environment_not_exists() {
        // given
        environmentRefQueryRepositoryStub.findReturns(null);
        environmentRefRepositoryStubSpy.expectNoUpdates();
        environmentRefRepositoryStubSpy.expectNoUpserts();
        environmentRefRepositoryStubSpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();

        var event = environmentCreatedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .environmentName("test")
                .status(EnvironmentStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(event);

        // then
        var actual = environmentRefRepositoryStubSpy.lastInserted();
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.environmentId()).isEqualTo(envId);
        assertThat(actual.status()).isEqualTo(EnvironmentStatus.ACTIVE);
        assertThat(actual.lastRevision()).isEqualTo(Revision.initialRevision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_update_environment_when_exists() {
        // given
        var existingEnv = fakeEnvironmentRefBuilder()
                .status(EnvironmentStatus.ACTIVE)
                .build();

        environmentRefQueryRepositoryStub.findReturns(existingEnv);
        environmentRefRepositoryStubSpy.expectNoInserts();
        environmentRefRepositoryStubSpy.expectNoUpserts();
        environmentRefRepositoryStubSpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = environmentStatusChangedEventBuilder()
                .projectId(existingEnv.projectId().uuid())
                .environmentId(existingEnv.environmentId().uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(existingEnv.lastRevision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = environmentRefRepositoryStubSpy.lastUpdated();
        assertThat(updated.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
        assertThat(updated.lastRevision()).isEqualTo(existingEnv.lastRevision().next());
    }

    @Test
    void should_publish_rebuild_requested_event_with_correct_ids_when_gap_detected() {
        var existing = fakeEnvironmentRefBuilder()
                .lastRevision(Revision.from(2))
                .build();

        environmentRefQueryRepositoryStub.findReturns(existing);
        environmentRefRepositoryStubSpy.expectNoInserts();
        environmentRefRepositoryStubSpy.expectNoUpserts();
        environmentRefRepositoryStubSpy.expectNoUpdates();
        environmentRefRepositoryStubSpy.markInconsistentIfNotMarkedReturns(true);

        var event = environmentStatusChangedEventBuilder()
                .projectId(existing.projectId().uuid())
                .environmentId(existing.environmentId().uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(5)
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getLastEvent(RebuildEnvironmentRefRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.projectId()).isEqualTo(existing.projectId());
        assertThat(internalEvent.environmentId()).isEqualTo(existing.environmentId());
    }

}