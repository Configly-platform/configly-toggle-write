package pl.feature.toggle.service.write.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static pl.feature.toggle.service.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

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

    @Test
    void should_return_true_when_toggle_with_name_exists_in_environment() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var toggle = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .name(FeatureToggleName.create("my.toggle"))
                .build();

        commandRepository.save(toggle);

        // when
        var result = sut.exists(toggle.name(), env.environmentId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void should_return_false_when_toggle_name_exists_but_in_other_environment() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);

        var env1 = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);
        var env2 = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var name = FeatureToggleName.create("my.toggle");

        var toggleInEnv1 = fakeFeatureToggleBuilder()
                .environmentId(env1.environmentId())
                .name(name)
                .build();

        commandRepository.save(toggleInEnv1);

        // when
        var result = sut.exists(name, env2.environmentId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_return_false_when_toggle_does_not_exist() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        // when
        var result = sut.exists(FeatureToggleName.create("missing.toggle"), env.environmentId());

        // then
        assertThat(result).isFalse();
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