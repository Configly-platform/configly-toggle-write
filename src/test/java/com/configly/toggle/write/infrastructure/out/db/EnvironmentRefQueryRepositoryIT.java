package com.configly.toggle.write.infrastructure.out.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.AbstractITTest;
import com.configly.toggle.write.application.port.out.EnvironmentRefProjectionRepository;
import com.configly.toggle.write.application.port.out.EnvironmentRefQueryRepository;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.domain.reference.ProjectRef;

import static org.assertj.core.api.Assertions.assertThat;
import static com.configly.toggle.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static com.configly.toggle.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

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
