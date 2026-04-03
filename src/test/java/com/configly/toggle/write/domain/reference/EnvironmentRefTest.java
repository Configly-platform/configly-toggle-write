package com.configly.toggle.write.domain.reference;

import org.junit.jupiter.api.Test;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.domain.reference.exception.CannotOperateOnArchivedEnvironmentException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class EnvironmentRefTest {

    @Test
    void should_create_from_raw_values() {
        // given
        var projectId = UUID.randomUUID();
        var envId = UUID.randomUUID();

        // when
        var ref = EnvironmentRef.from(projectId, envId, EnvironmentStatus.ACTIVE.name(), 7);

        // then
        assertThat(ref.projectId().uuid()).isEqualTo(projectId);
        assertThat(ref.environmentId().uuid()).isEqualTo(envId);
        assertThat(ref.status()).isEqualTo(EnvironmentStatus.ACTIVE);
        assertThat(ref.lastRevision()).isEqualTo(Revision.from(7));
        assertThat(ref.consistent()).isTrue();
    }

    @Test
    void should_not_throw_when_environment_is_active() {
        var ref = EnvironmentRef.from(ProjectId.create(), EnvironmentId.create(), EnvironmentStatus.ACTIVE, Revision.from(1));
        ref.assertIsActive();
    }

    @Test
    void should_throw_when_environment_is_archived() {
        var ref = EnvironmentRef.from(ProjectId.create(), EnvironmentId.create(), EnvironmentStatus.ARCHIVED, Revision.from(1));

        var ex = catchException(ref::assertIsActive);

        assertThat(ex).isInstanceOf(CannotOperateOnArchivedEnvironmentException.class);
    }

    @Test
    void should_apply_status_and_revision_and_set_consistent_true() {
        var ref = EnvironmentRef.from(ProjectId.create(), EnvironmentId.create(), EnvironmentStatus.ACTIVE, Revision.from(1));

        var updated = ref.apply(EnvironmentStatus.ARCHIVED, Revision.from(2));

        assertThat(updated.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
        assertThat(updated.lastRevision()).isEqualTo(Revision.from(2));
        assertThat(updated.consistent()).isTrue();
        assertThat(updated.environmentId()).isEqualTo(ref.environmentId());
        assertThat(updated.projectId()).isEqualTo(ref.projectId());
    }

}