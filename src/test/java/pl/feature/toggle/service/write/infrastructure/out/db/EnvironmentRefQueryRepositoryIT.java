package pl.feature.toggle.service.write.infrastructure.out.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class EnvironmentRefQueryRepositoryIT extends AbstractITTest {

    private ProjectRef projectRef;

    @Autowired
    private EnvironmentRefQueryRepository sut;

    @Autowired
    private EnvironmentRefProjectionRepository projectionRepository;

    @Autowired
    private ProjectRefProjectionRepository projectRefProjectionRepository;

    @BeforeEach
    void setUp() {
        projectRef = createProjectRef(ProjectStatus.ACTIVE);
    }

    @Test
    void should_find_consistent_environment_ref_when_consistent() {
        // given
        var ref = fakeEnvironmentRefBuilder()
                .projectId(projectRef.projectId())
                .consistent(true)
                .build();
        projectionRepository.insert(ref);

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
        projectionRepository.insert(ref);

        // when
        var result = sut.findConsistent(ref.projectId(), ref.environmentId());

        // then
        assertThat(result).isEmpty();
    }

    private ProjectRef createProjectRef(ProjectStatus projectStatus) {
        var projectRef = fakeProjectRefBuilder()
                .status(projectStatus)
                .build();
        projectRefProjectionRepository.insert(projectRef);
        return projectRef;
    }
}
