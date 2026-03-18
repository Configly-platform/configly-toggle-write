package pl.feature.toggle.service.write.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.value.FeatureToggleValueSnapshot;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleUpdateFailedException;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import static org.assertj.core.api.Assertions.*;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static pl.feature.toggle.service.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;
import static pl.feature.toggle.service.write.builder.FakeProjectRefBuilder.fakeProjectRefBuilder;

class FeatureToggleCommandRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleCommandRepository sut;

    @Autowired
    private EnvironmentRefProjectionRepository environmentRefProjectionRepository;

    @Autowired
    private ProjectRefProjectionRepository projectRefProjectionRepository;

    @Autowired
    private FeatureToggleQueryRepository queryRepository;


    @Test
    void should_save_feature_toggle() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var toggle = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .build();

        // when
        sut.save(toggle);

        // then
        var actual = queryRepository.getOrThrow(toggle.id());
        assertThat(actual).isEqualTo(toggle);
    }

    @Test
    void should_update_feature_toggle_when_expected_revision_matches() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var original = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .build();
        sut.save(original);

        var updateResult = original.changeValue(new FeatureToggleValueSnapshot("FALSE"));
        assertThat(updateResult.wasUpdated()).isTrue();

        // when
        sut.update(updateResult);

        // then
        var actual = queryRepository.getOrThrow(original.id());
        assertThat(actual).isEqualTo(updateResult.featureToggle());
    }

    @Test
    void should_throw_and_not_update_when_expected_revision_does_not_match() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var original = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .build();
        sut.save(original);

        var validUpdate = original.changeValue(new FeatureToggleValueSnapshot("TRUE"));

        var wrongExpectedRevision = validUpdate.expectedRevision().next();
        var invalidUpdate = FeatureToggleUpdateResult.of(
                validUpdate.featureToggle(),
                wrongExpectedRevision,
                validUpdate.changes()
        );

        // when / then
        assertThatThrownBy(() -> sut.update(invalidUpdate))
                .isInstanceOf(FeatureToggleUpdateFailedException.class);

        var actual = queryRepository.getOrThrow(original.id());
        assertThat(actual).isEqualTo(original);
    }

    @Test
    void should_fail_on_second_update_attempt_for_same_expected_revision() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var original = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .build();
        sut.save(original);

        var updateResult = original.changeValue(new FeatureToggleValueSnapshot("FALSE"));

        // when
        sut.update(updateResult);

        // then
        assertThatThrownBy(() -> sut.update(updateResult))
                .isInstanceOf(FeatureToggleUpdateFailedException.class);
    }

    @Test
    void should_throw_exception_when_create_and_feature_toggle_name_is_not_unique() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var original = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .build();
        sut.save(original);

        // when
        var exception = catchException(() -> sut.save(original));

        // then
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(FeatureToggleAlreadyExistsException.class);
    }

    @Test
    void should_throw_exception_when_update_feature_toggle_and_name_is_not_unique() {
        // given
        var project = createProjectRef(ProjectStatus.ACTIVE);
        var env = createEnvironmentRef(project.projectId(), EnvironmentStatus.ACTIVE);

        var first = fakeFeatureToggleBuilder()
                .environmentId(env.environmentId())
                .build();
        sut.save(first);
        var second = fakeFeatureToggleBuilder()
                .name(FeatureToggleName.create("XXXX"))
                .environmentId(env.environmentId())
                .build();
        sut.save(second);
        var updateResult = second.update(first.name(), first.description());

        // when
        var exception = catchException(() -> sut.update(updateResult));
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(FeatureToggleAlreadyExistsException.class);
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
