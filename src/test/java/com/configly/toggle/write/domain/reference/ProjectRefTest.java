package com.configly.toggle.write.domain.reference;

import org.junit.jupiter.api.Test;
import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.domain.reference.exception.CannotOperateOnArchivedProjectException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class ProjectRefTest {

    @Test
    void should_create_from_raw_values() {
        // given
        var projectIdRaw = UUID.randomUUID();

        // when
        var ref = ProjectRef.from(projectIdRaw, ProjectStatus.ACTIVE.name(), 7);

        // then
        assertThat(ref.projectId().uuid()).isEqualTo(projectIdRaw);
        assertThat(ref.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(ref.lastRevision()).isEqualTo(Revision.from(7));
        assertThat(ref.consistent()).isTrue();
    }

    @Test
    void should_not_throw_when_project_is_active() {
        // given
        var ref = ProjectRef.from(ProjectId.create(), ProjectStatus.ACTIVE, Revision.from(1));

        // when && then
        ref.assertIsActive();
    }

    @Test
    void should_throw_when_project_is_archived() {
        // given
        var ref = ProjectRef.from(ProjectId.create(), ProjectStatus.ARCHIVED, Revision.from(1));

        // when
        var ex = catchException(ref::assertIsActive);

        // then
        assertThat(ex).isInstanceOf(CannotOperateOnArchivedProjectException.class);
    }

    @Test
    void should_apply_status_and_revision_and_set_consistent_true() {
        // given
        var projectId = ProjectId.create();
        var ref = ProjectRef.from(projectId, ProjectStatus.ACTIVE, Revision.from(1));

        // when
        var updated = ref.apply(ProjectStatus.ARCHIVED, Revision.from(2));

        // then
        assertThat(updated.projectId()).isEqualTo(projectId);
        assertThat(updated.status()).isEqualTo(ProjectStatus.ARCHIVED);
        assertThat(updated.lastRevision()).isEqualTo(Revision.from(2));
        assertThat(updated.consistent()).isTrue();
    }

    @Test
    void should_recognize_active_and_archived() {
        // given
        var active = ProjectRef.from(ProjectId.create(), ProjectStatus.ACTIVE, Revision.from(1));
        var archived = ProjectRef.from(ProjectId.create(), ProjectStatus.ARCHIVED, Revision.from(1));

        // then
        assertThat(active.isActive()).isTrue();
        assertThat(active.isArchived()).isFalse();

        assertThat(archived.isActive()).isFalse();
        assertThat(archived.isArchived()).isTrue();
    }
}
