package pl.feature.toggle.service.write.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.CannotOperateOnArchivedFeatureToggleException;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedEnvironmentException;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedProjectException;
import pl.feature.toggle.service.write.domain.reference.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.builder.FakeUpdateFeatureToggleCommandBuilder.fakeUpdateFeatureToggleCommandBuilder;

class UpdateFeatureToggleHandlerTest extends AbstractUnitTest {

    private UpdateFeatureToggleUseCase sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.updateFeatureToggleUseCase(
                featureToggleCommandRepositorySpy,
                featureToggleQueryRepositoryStub,
                featureTogglePolicyFacade,
                projectRefConsistencySpy,
                environmentRefConsistencySpy,
                outboxWriter,
                actorProvider,
                correlationProvider
        );

    }

    @Test
    void should_update_feature_toggle() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.existsReturns(false);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ACTIVE_FEATURE_TOGGLE);

        // when
        sut.execute(command);

        // then
        var updated = featureToggleCommandRepositorySpy.lastUpdated();
        assertThat(updated.featureToggle().name()).isEqualTo(command.name());
        assertThat(updated.featureToggle().description()).isEqualTo(command.description());
        assertContainsEventOfType(FEATURE_TOGGLE.topic(), FeatureToggleUpdated.class);
    }

    @Test
    void should_not_update_feature_toggle_when_project_is_archived() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ARCHIVED_PROJECT);
        featureToggleQueryRepositoryStub.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedProjectException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_update_feature_toggle_when_environment_is_archived() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.expectNoCalls();
        projectRefConsistencySpy.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));


        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedEnvironmentException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_update_feature_toggle_when_project_doesnt_exist() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedThrows(new ProjectNotFoundException(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId()));
        featureToggleQueryRepositoryStub.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));


        // then
        assertThat(exception).isInstanceOf(ProjectNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_update_feature_toggle_when_environment_doesnt_exist() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedThrows(new EnvironmentNotFoundException(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId(),
                ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId()));
        featureToggleQueryRepositoryStub.expectNoCalls();
        projectRefConsistencySpy.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));


        // then
        assertThat(exception).isInstanceOf(EnvironmentNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }


    @Test
    void should_not_update_feature_toggle_when_name_is_not_unique_for_environment() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.existsReturns(true);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ACTIVE_FEATURE_TOGGLE);
        featureToggleCommandRepositorySpy.expectNoCalls();


        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(FeatureToggleAlreadyExistsException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_update_feature_toggle_when_toggle_is_archived() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.existsReturns(false);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ARCHIVED_FEATURE_TOGGLE);


        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedFeatureToggleException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_update_feature_toggle_when_no_changes() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .withName(ACTIVE_FEATURE_TOGGLE.name())
                .withDescription(ACTIVE_FEATURE_TOGGLE.description())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.existsReturns(false);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ACTIVE_FEATURE_TOGGLE);
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        sut.execute(command);

        // then
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_update_when_toggle_not_exists() {
        // given
        var command = fakeUpdateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowThrows(new FeatureToggleNotFoundException(command.featureToggleId()));
        featureToggleCommandRepositorySpy.expectNoCalls();


        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(FeatureToggleNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }
}
