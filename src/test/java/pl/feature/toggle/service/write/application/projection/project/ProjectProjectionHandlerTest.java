package pl.feature.toggle.service.write.application.projection.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.projection.project.event.RebuildProjectRefRequested;
import pl.feature.toggle.service.write.domain.reference.ProjectStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.project.ProjectCreated.projectCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged.projectStatusChangedEventBuilder;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class ProjectProjectionHandlerTest extends AbstractUnitTest {

    private ProjectProjection sut;

    @BeforeEach
    void setUp() {
        sut = ProjectProjectionFacade.projectProjection(
                projectRefRepositoryStubSpy,
                projectRefQueryRepositoryStub,
                applicationEventPublishedSpy,
                revisionProjectionApplier
        );
    }

    @Test
    void should_insert_new_project_when_project_not_exists() {
        // given
        projectRefQueryRepositoryStub.findReturns(null);
        projectRefRepositoryStubSpy.expectNoUpdates();
        projectRefRepositoryStubSpy.expectNoUpserts();
        projectRefRepositoryStubSpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var projectId = ProjectId.create();

        var event = projectCreatedEventBuilder()
                .projectId(projectId.uuid())
                .status(ProjectStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(event);

        // then
        var actual = projectRefRepositoryStubSpy.lastInserted();
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(actual.lastRevision()).isEqualTo(Revision.initialRevision());
        assertThat(actual.consistent()).isTrue();
    }

    @Test
    void should_update_project_when_exists() {
        var existing = fakeProjectRefBuilder()
                .status(ProjectStatus.ACTIVE)
                .build();

        projectRefQueryRepositoryStub.findReturns(existing);
        projectRefRepositoryStubSpy.expectNoInserts();
        projectRefRepositoryStubSpy.expectNoUpserts();
        projectRefRepositoryStubSpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = projectStatusChangedEventBuilder()
                .projectId(existing.projectId().uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(existing.lastRevision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = projectRefRepositoryStubSpy.lastUpdated();
        assertThat(updated.status()).isEqualTo(ProjectStatus.ARCHIVED);
        assertThat(updated.lastRevision()).isEqualTo(existing.lastRevision().next());
    }

    @Test
    void should_publish_rebuild_requested_event_with_correct_id_when_gap_detected() {
        var existing = fakeProjectRefBuilder()
                .lastRevision(Revision.from(2))
                .build();

        projectRefQueryRepositoryStub.findReturns(existing);
        projectRefRepositoryStubSpy.expectNoInserts();
        projectRefRepositoryStubSpy.expectNoUpserts();
        projectRefRepositoryStubSpy.expectNoUpdates();
        projectRefRepositoryStubSpy.markInconsistentIfNotMarkedReturns(true);

        var event = projectStatusChangedEventBuilder()
                .projectId(existing.projectId().uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(5) // gap
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getLastEvent(RebuildProjectRefRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.projectId()).isEqualTo(existing.projectId());
    }
}