package pl.feature.toggle.service.write.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class ProjectRefProjectionRepositoryIT extends AbstractITTest {

    @Autowired
    private ProjectRefProjectionRepository sut;

    @Autowired
    private ProjectRefQueryRepository queryRepository;

    @Test
    void should_save_project_ref() {
        // given
        var projectRef = fakeProjectRefBuilder().build();

        // when
        sut.insert(projectRef);

        // then
        var actual = queryRepository.find(projectRef.projectId()).orElseThrow();
        assertThat(actual).isEqualTo(projectRef);
    }

    @Test
    void should_update_project_ref() {
        // given
        var original = fakeProjectRefBuilder()
                .lastRevision(Revision.from(1))
                .build();
        sut.insert(original);

        var updated = original.apply(ProjectStatus.ACTIVE, original.lastRevision().next());

        // when
        sut.update(updated);

        // then
        var actual = queryRepository.find(original.projectId()).orElseThrow();
        assertThat(actual).isEqualTo(updated);
    }

    @Test
    void should_upsert_and_insert_when_not_exists() {
        // given
        var projectRef = fakeProjectRefBuilder().build();

        // when
        sut.upsert(projectRef);

        // then
        var actual = queryRepository.find(projectRef.projectId()).orElseThrow();
        assertThat(actual).isEqualTo(projectRef);
    }

    @Test
    void should_upsert_and_update_when_exists() {
        // given
        var original = fakeProjectRefBuilder()
                .lastRevision(Revision.from(1))
                .build();
        sut.insert(original);

        var updated = original.apply(ProjectStatus.ACTIVE, original.lastRevision().next());

        // when
        sut.upsert(updated);

        // then
        var actual = queryRepository.find(original.projectId()).orElseThrow();
        assertThat(actual).isEqualTo(updated);
    }

    @Test
    void should_mark_project_as_inconsistent_when_not_marked() {
        // given
        var projectRef = fakeProjectRefBuilder()
                .consistent(true)
                .build();
        sut.insert(projectRef);

        // when
        var result = sut.markInconsistentIfNotMarked(projectRef.projectId());

        // then
        assertThat(result).isTrue();
        assertThat(queryRepository.findConsistent(projectRef.projectId())).isEmpty();
    }

    @Test
    void should_not_mark_project_as_inconsistent_twice() {
        // given
        var projectRef = fakeProjectRefBuilder()
                .consistent(true)
                .build();
        sut.insert(projectRef);
        sut.markInconsistentIfNotMarked(projectRef.projectId());

        // when
        var result = sut.markInconsistentIfNotMarked(projectRef.projectId());

        // then
        assertThat(result).isFalse();
    }

}