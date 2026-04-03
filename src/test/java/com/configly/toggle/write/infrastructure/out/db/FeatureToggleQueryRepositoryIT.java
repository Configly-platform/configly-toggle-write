package com.configly.toggle.write.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.AbstractITTest;
import com.configly.toggle.write.application.port.out.EnvironmentRefProjectionRepository;
import com.configly.toggle.write.application.port.out.FeatureToggleCommandRepository;
import com.configly.toggle.write.application.port.out.FeatureToggleQueryRepository;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import com.configly.toggle.write.domain.reference.EnvironmentRef;
import com.configly.toggle.write.domain.reference.ProjectRef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.configly.toggle.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static com.configly.toggle.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;
import static com.configly.toggle.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class FeatureToggleQueryRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleQueryRepository sut;

    @Autowired
    private FeatureToggleCommandRepository commandRepository;

    @Autowired
    private EnvironmentRefProjectionRepository environmentRefProjectionRepository;

    @Autowired
    private ProjectRefProjectionRepository projectRefProjectionRepository;

    @Test
    void should_get_feature_toggle_or_throw_when_exists() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var toggle = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .build();

        commandRepository.save(toggle);

        // when
        var result = sut.getOrThrow(toggle.id());

        // then
        assertThat(result).isEqualTo(toggle);
    }

    @Test
    void should_throw_when_feature_toggle_not_found() {
        // given
        var missingId = FeatureToggleId.create();

        // when / then
        assertThatThrownBy(() -> sut.getOrThrow(missingId))
                .isInstanceOf(FeatureToggleNotFoundException.class);
    }

    private EnvironmentRef createEnvironmentRef(ProjectId projectId, EnvironmentStatus environmentStatus) {
        var environmentRef = fakeEnvironmentRefBuilder()
                .projectId(projectId)
                .status(environmentStatus)
                .build();
        environmentRefProjectionRepository.insert(environmentRef);
        return environmentRef;
    }

    private ProjectRef createProjectRef(ProjectStatus projectStatus) {
        var projectRef = fakeProjectRefBuilder()
                .status(projectStatus)
                .build();
        projectRefProjectionRepository.insert(projectRef);
        return projectRef;
    }


}