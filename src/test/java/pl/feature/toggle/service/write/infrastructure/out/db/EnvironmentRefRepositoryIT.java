package pl.feature.toggle.service.write.infrastructure.out.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.reference.EnvironmentStatus;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.domain.reference.ProjectStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class EnvironmentRefRepositoryIT extends AbstractITTest {

    @Autowired
    private EnvironmentRefRepository sut;

    @Autowired
    private ProjectRefRepository projectRefRepository;

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
        sut.upsert(environmentRef);

        // when
        var result = sut.find(environmentRef.projectId(), environmentRef.environmentId());

        // then
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
        assertThat(sut.find(ref.projectId(), ref.environmentId())).contains(ref);
    }

    @Test
    void should_find_consistent_environment_ref_when_consistent() {
        // given
        var ref = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .consistent(true)
                .build();
        sut.insert(ref);

        // when
        var result = sut.findConsistent(ref.projectId(), ref.environmentId());

        // then
        assertThat(result).contains(ref);
    }

    @Test
    void should_not_find_consistent_environment_ref_when_inconsistent() {
        // given
        var ref = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .consistent(false)
                .build();
        sut.insert(ref);

        // when
        var result = sut.findConsistent(ref.projectId(), ref.environmentId());

        // then
        assertThat(result).isEmpty();
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
        assertThat(sut.find(original.projectId(), original.environmentId()))
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
        assertThat(sut.find(ref.projectId(), ref.environmentId())).contains(ref);
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
        assertThat(sut.find(original.projectId(), original.environmentId()))
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
        assertThat(sut.findConsistent(ref.projectId(), ref.environmentId())).isEmpty();
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
        projectRefRepository.insert(projectRef);
        return projectRef;
    }
}