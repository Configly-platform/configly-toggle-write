package com.configly.toggle.write.application.projection.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.AbstractUnitTest;
import com.configly.toggle.write.application.port.in.ProjectProjection;
import com.configly.toggle.write.application.projection.project.event.RebuildProjectRefRequested;

import static org.assertj.core.api.Assertions.assertThat;
import static com.configly.contracts.fake.event.FakeProjectCreatedBuilder.fakeProjectCreatedBuilder;
import static com.configly.contracts.fake.event.FakeProjectStatusChangedBuilder.fakeProjectStatusChangedBuilder;
import static com.configly.toggle.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class ProjectProjectionHandlerTest extends AbstractUnitTest {

    private ProjectProjection sut;

    @BeforeEach
    void setUp() {
        sut = ProjectProjectionFacade.projectProjection(
                projectRefRepositorySpy,
                projectRefQueryRepositoryStub,
                applicationEventPublishedSpy,
                revisionProjectionApplier
        );
    }

    @Test
    void should_insert_new_project_when_project_not_exists() {
        // given
        projectRefQueryRepositoryStub.findReturns(null);
        projectRefRepositorySpy.expectNoUpdates();
        projectRefRepositorySpy.expectNoUpserts();
        projectRefRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var projectId = ProjectId.create();

        var event = fakeProjectCreatedBuilder()
                .withProjectId(projectId.uuid())
                .withStatus(ProjectStatus.ACTIVE.name())
                .withRevision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(event);

        // then
        var actual = projectRefRepositorySpy.lastInserted();
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
        projectRefRepositorySpy.expectNoInserts();
        projectRefRepositorySpy.expectNoUpdates();
        projectRefRepositorySpy.expectNoMarkInconsistent();
        applicationEventPublishedSpy.expectNoEvents();

        var event = fakeProjectStatusChangedBuilder()
                .withProjectId(existing.projectId().uuid())
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(existing.lastRevision().next().value())
                .build();

        // when
        sut.handle(event);

        // then
        var updated = projectRefRepositorySpy.lastUpserted();
        assertThat(updated.status()).isEqualTo(ProjectStatus.ARCHIVED);
        assertThat(updated.lastRevision()).isEqualTo(existing.lastRevision().next());
    }

    @Test
    void should_publish_rebuild_requested_event_with_correct_id_when_gap_detected() {
        var existing = fakeProjectRefBuilder()
                .lastRevision(Revision.from(2))
                .build();

        projectRefQueryRepositoryStub.findReturns(existing);
        projectRefRepositorySpy.expectNoInserts();
        projectRefRepositorySpy.expectNoUpserts();
        projectRefRepositorySpy.expectNoUpdates();
        projectRefRepositorySpy.markInconsistentIfNotMarkedReturns(true);

        var event = fakeProjectStatusChangedBuilder()
                .withProjectId(existing.projectId().uuid())
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(5)
                .build();

        // when
        sut.handle(event);

        // then
        var internalEvent = applicationEventPublishedSpy.getLastEvent(RebuildProjectRefRequested.class);
        assertThat(internalEvent).isNotNull();
        assertThat(internalEvent.projectId()).isEqualTo(existing.projectId());
    }
}