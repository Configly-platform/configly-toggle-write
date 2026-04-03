package com.configly.toggle.write.infrastructure.out.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.AbstractITTest;
import com.configly.toggle.write.application.port.out.EnvironmentRefProjectionRepository;
import com.configly.toggle.write.application.port.out.EnvironmentRefQueryRepository;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.domain.reference.ProjectRef;

import static org.assertj.core.api.Assertions.assertThat;
import static com.configly.toggle.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static com.configly.toggle.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class EnvironmentRefProjectionRepositoryIT extends AbstractITTest {

    @Autowired
    private EnvironmentRefProjectionRepository sut;

    @Autowired
    private EnvironmentRefQueryRepository queryRepository;

    @Autowired
    private ProjectRefProjectionRepository projectRefProjectionRepository;

    private ProjectRef projectRef;

    @BeforeEach
    void setUp() {
        projectRef = createProjectRef(ProjectStatus.ACTIVE);
    }

    @Test
    void should_save_environment_ref() {
        // given
        var environmentRef = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .build();

        // when
        sut.upsert(environmentRef);

        // then
        var result = queryRepository.find(environmentRef.projectId(), environmentRef.environmentId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(environmentRef);
    }

    @Test
    void should_insert_and_find_environment_ref() {
        // given
        var ref = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .build();

        // when
        sut.insert(ref);

        // then
        assertThat(queryRepository.find(ref.projectId(), ref.environmentId())).contains(ref);
    }

    @Test
    void should_update_environment_ref() {
        // given
        var original = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .lastRevision(Revision.from(1))
                .build();
        sut.insert(original);

        var updated = original.apply(EnvironmentStatus.ACTIVE, original.lastRevision().next());

        // when
        sut.update(updated);

        // then
        assertThat(queryRepository.find(original.projectId(), original.environmentId()))
                .contains(updated);
    }

    @Test
    void should_upsert_and_insert_when_not_exists() {
        // given
        var ref = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .build();

        // when
        sut.upsert(ref);

        // then
        assertThat(queryRepository.find(ref.projectId(), ref.environmentId())).contains(ref);
    }

    @Test
    void should_upsert_and_update_when_exists() {
        // given
        var original = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .lastRevision(Revision.from(1))
                .build();
        sut.insert(original);

        var updated = original.apply(EnvironmentStatus.ACTIVE, original.lastRevision().next());

        // when
        sut.upsert(updated);

        // then
        assertThat(queryRepository.find(original.projectId(), original.environmentId()))
                .contains(updated);
    }

    @Test
    void should_mark_environment_as_inconsistent_when_not_marked() {
        // given
        var ref = fakeEnvironmentRefBuilder()
                .consistent(true)
                .projectId(projectRef.projectId())
                .build();
        sut.insert(ref);

        // when
        var result = sut.markInconsistentIfNotMarked(ref.environmentId());

        // then
        assertThat(result).isTrue();
        assertThat(queryRepository.findConsistent(ref.projectId(), ref.environmentId())).isEmpty();
    }

    @Test
    void should_not_mark_environment_as_inconsistent_twice() {
        // given
        var ref = fakeEnvironmentRefBuilder()
                .consistent(true)
                .projectId(projectRef.projectId())
                .build();
        sut.insert(ref);
        sut.markInconsistentIfNotMarked(ref.environmentId());

        // when
        var result = sut.markInconsistentIfNotMarked(ref.environmentId());

        // then
        assertThat(result).isFalse();
    }

    private ProjectRef createProjectRef(ProjectStatus projectStatus) {
        var projectRef = fakeProjectRefBuilder()
                .status(projectStatus)
                .build();
        projectRefProjectionRepository.insert(projectRef);
        return projectRef;
    }
}