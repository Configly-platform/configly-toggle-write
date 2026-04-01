package pl.feature.toggle.service.write.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.ChangeFeatureToggleStatusUseCase;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedEnvironmentException;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedProjectException;
import pl.feature.toggle.service.write.domain.reference.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.builder.FakeChangeFeatureToggleStatusCommandBuilder.fakeChangeFeatureToggleStatusCommandBuilder;

class ChangeFeatureToggleStatusHandlerTest extends AbstractUnitTest {

    private ChangeFeatureToggleStatusUseCase sut;

    @BeforeEach
    void setup() {
        sut = FeatureToggleHandlerFacade.changeFeatureToggleStatusUseCase(
                featureToggleCommandRepositorySpy,
                featureToggleQueryRepositoryStub,
                projectRefConsistencySpy,
                environmentRefConsistencySpy,
                outboxWriter
        );
    }

    @Test
    void should_change_feature_toggle_status_to_archived_when_active() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ARCHIVED)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ACTIVE_FEATURE_TOGGLE);

        // when
        sut.handle(command);

        // then
        var updated = featureToggleCommandRepositorySpy.lastUpdated();
        assertThat(updated.featureToggle().isArchived()).isTrue();
        assertThat(updated.featureToggle().isActive()).isFalse();
        assertContainsEventOfType(FEATURE_TOGGLE.topicName(), FeatureToggleStatusChanged.class);
        var event = getLastPublishedEvent(FEATURE_TOGGLE.topicName(), FeatureToggleStatusChanged.class);
        assertThat(event.status()).isEqualTo(FeatureToggleStatus.ARCHIVED.name());
    }

    @Test
    void should_change_feature_toggle_status_to_active_when_archived() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ACTIVE)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ARCHIVED_FEATURE_TOGGLE);

        // when
        sut.handle(command);

        // then
        var updated = featureToggleCommandRepositorySpy.lastUpdated();
        assertThat(updated.featureToggle().isArchived()).isFalse();
        assertThat(updated.featureToggle().isActive()).isTrue();
        assertContainsEventOfType(FEATURE_TOGGLE.topicName(), FeatureToggleStatusChanged.class);
        var event = getLastPublishedEvent(FEATURE_TOGGLE.topicName(), FeatureToggleStatusChanged.class);
        assertThat(event.status()).isEqualTo(FeatureToggleStatus.ACTIVE.name());
    }

    @Test
    void should_do_nothing_when_change_toggle_status_to_active_when_active() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ACTIVE)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ACTIVE_FEATURE_TOGGLE);
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        sut.handle(command);

        // then
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_do_nothing_when_change_toggle_status_to_archived_when_archived() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ARCHIVED)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ARCHIVED_FEATURE_TOGGLE);
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        sut.handle(command);

        // then
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_throw_exception_when_change_status_and_project_is_archived() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ARCHIVED)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ARCHIVED_PROJECT);
        featureToggleQueryRepositoryStub.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.handle(command));

        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedProjectException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_throw_exception_when_change_status_and_environment_is_archived() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ARCHIVED)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.expectNoCalls();
        featureToggleQueryRepositoryStub.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.handle(command));

        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedEnvironmentException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_throw_exception_when_environment_not_found() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ARCHIVED)
                .build();
        environmentRefConsistencySpy.getTrustedThrows(new EnvironmentNotFoundException(command.projectId(), command.environmentId()));
        projectRefConsistencySpy.expectNoCalls();
        featureToggleQueryRepositoryStub.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.handle(command));

        // then
        assertThat(exception).isInstanceOf(EnvironmentNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_throw_exception_when_project_not_found() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ARCHIVED)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedThrows(new ProjectNotFoundException(command.projectId()));
        featureToggleQueryRepositoryStub.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.handle(command));

        // then
        assertThat(exception).isInstanceOf(ProjectNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_throw_exception_when_feature_toggle_not_found() {
        // given
        var command = fakeChangeFeatureToggleStatusCommandBuilder()
                .withNewStatus(FeatureToggleStatus.ARCHIVED)
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowThrows(new FeatureToggleNotFoundException(command.featureToggleId()));
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.handle(command));

        // then
        assertThat(exception).isInstanceOf(FeatureToggleNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }
}
