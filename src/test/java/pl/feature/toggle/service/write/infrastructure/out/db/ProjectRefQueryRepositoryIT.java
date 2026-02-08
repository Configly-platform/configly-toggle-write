package pl.feature.toggle.service.write.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class ProjectRefQueryRepositoryIT extends AbstractITTest {

    @Autowired
    private ProjectRefQueryRepository sut;

    @Autowired
    private ProjectRefProjectionRepository projectionRepository;


    @Test
    void should_find_project_ref() {
        // given
        var projectRef = fakeProjectRefBuilder().build();
        projectionRepository.insert(projectRef);

        // when
        var result = sut.find(projectRef.projectId()).orElseThrow();

        // then
        assertThat(result).isEqualTo(projectRef);
    }

    @Test
    void should_find_consistent_project_ref_when_consistent() {
        // given
        var projectRef = fakeProjectRefBuilder()
                .consistent(true)
                .build();
        projectionRepository.insert(projectRef);

        // when
        var result = sut.findConsistent(projectRef.projectId());

        // then
        assertThat(result).contains(projectRef);
    }

    @Test
    void should_not_find_project_ref_when_inconsistent() {
        // given
        var projectRef = fakeProjectRefBuilder()
                .consistent(false)
                .build();
        projectionRepository.insert(projectRef);

        // when
        var result = sut.findConsistent(projectRef.projectId());

        // then
        assertThat(result).isEmpty();
    }
}
