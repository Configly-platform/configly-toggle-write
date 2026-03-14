package pl.feature.toggle.service.write.application.projection.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.projection.environment.event.EnvironmentArchivedCascadeRequest;
import pl.feature.toggle.service.write.application.projection.environment.event.RebuildEnvironmentRefRequested;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged.environmentStatusChangedEventBuilder;
import static pl.feature.toggle.service.contracts.fake.event.FakeEnvironmentStatusChangedBuilder.fakeEnvironmentStatusChangedBuilder;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;

class EnvironmentProjectionHandlerTest extends AbstractUnitTest {

    private EnvironmentProjection sut;

    @BeforeEach
    void setUp() {
        sut = EnvironmentProjectionFacade.environmentProjection(
                environmentRefRepositorySpy,
                environmentRefQueryRepositoryStub,
                revisionProjectionApplier,
                applicationEventPublishedSpy
        );
    }

    @Test
    void should_insert_new_environment_when_environment_not_exists() {
        // given
        environmentRefQueryRepositoryStub.findReturns(null);
        environmentRefRepositorySpy.expectNoUpdates();
        environmentRefRepositorySpy.expectNoUpserts();
        environmentRefRepositorySpy.expectNoMarkInconsistent();
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
        var actual = environmentRefRepositorySpy.lastInserted();
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.environmentId()).isEqualTo(envId);
        assertThat(actual.status()).isEqualTo(EnvironmentStatus.ACTIVE);
        assertThat(actual.lastRevision()).isEqualTo(Revision.initialRevision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_update_environment_status_when_exists_to_archived_and_emit_event() {
        // given
        var existingEnv = fakeEnvironmentRefBuilder()
                .status(EnvironmentStatus.ACTIVE)
                .build();

        environmentRefQueryRepositoryStub.findReturns(existingEnv);
        environmentRefRepositorySpy.expectNoInserts();
        environmentRefRepositorySpy.expectNoUpdates();
        environmentRefRepositorySpy.expectNoMarkInconsistent();

        var event = fakeEnvironmentStatusChangedBuilder()
                .withProjectId(existingEnv.projectId().uuid())
                .withEnvironmentId(existingEnv.environmentId().uuid())
                .withStatus(EnvironmentStatus.ARCHIVED.name())
                .withRevision(existingEnv.lastRevision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = environmentRefRepositorySpy.lastUpserted();
        assertThat(updated.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
        assertThat(updated.lastRevision()).isEqualTo(existingEnv.lastRevision().next());
        var internalEvent = applicationEventPublishedSpy.getLastEvent(EnvironmentArchivedCascadeRequest.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent).isEqualTo(EnvironmentArchivedCascadeRequest.create(existingEnv.environmentId(), event.metadata()));
    }

    @Test
    void should_update_environment_status_when_exists_to_active() {
        // given
        var existingEnv = fakeEnvironmentRefBuilder()
                .status(EnvironmentStatus.ARCHIVED)
                .build();

        environmentRefQueryRepositoryStub.findReturns(existingEnv);
        environmentRefRepositorySpy.expectNoInserts();
        environmentRefRepositorySpy.expectNoUpdates();
        environmentRefRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = environmentStatusChangedEventBuilder()
                .projectId(existingEnv.projectId().uuid())
                .environmentId(existingEnv.environmentId().uuid())
                .status(EnvironmentStatus.ACTIVE.name())
                .revision(existingEnv.lastRevision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = environmentRefRepositorySpy.lastUpserted();
        assertThat(updated.status()).isEqualTo(EnvironmentStatus.ACTIVE);
        assertThat(updated.lastRevision()).isEqualTo(existingEnv.lastRevision().next());
    }

    @Test
    void should_not_publish_event_when_update_environment_status_to_archived_but_operation_is_not_applied() {
        var existing = fakeEnvironmentRefBuilder()
                .status(EnvironmentStatus.ACTIVE)
                .lastRevision(Revision.from(2))
                .build();

        environmentRefQueryRepositoryStub.findReturns(existing);
        environmentRefRepositorySpy.expectNoInserts();
        environmentRefRepositorySpy.expectNoUpserts();
        environmentRefRepositorySpy.expectNoUpdates();
        environmentRefRepositorySpy.markInconsistentIfNotMarkedReturns(true);

        var event = environmentStatusChangedEventBuilder()
                .projectId(existing.projectId().uuid())
                .environmentId(existing.environmentId().uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(5)
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getEvent(RebuildEnvironmentRefRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.projectId()).isEqualTo(existing.projectId());
        assertThat(internalEvent.environmentId()).isEqualTo(existing.environmentId());
        applicationEventPublishedSpy.notContainsEventOfType(EnvironmentArchivedCascadeRequest.class);
    }

    @Test
    void should_publish_rebuild_requested_event_with_correct_ids_when_gap_detected() {
        var existing = fakeEnvironmentRefBuilder()
                .lastRevision(Revision.from(2))
                .build();

        environmentRefQueryRepositoryStub.findReturns(existing);
        environmentRefRepositorySpy.expectNoInserts();
        environmentRefRepositorySpy.expectNoUpserts();
        environmentRefRepositorySpy.expectNoUpdates();
        environmentRefRepositorySpy.markInconsistentIfNotMarkedReturns(true);

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