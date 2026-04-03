package com.configly.toggle.write.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.configly.contracts.event.featuretoggle.FeatureToggleValueChanged;
import com.configly.value.FeatureToggleValueBuilder;
import com.configly.value.FeatureToggleValueType;
import com.configly.toggle.write.AbstractUnitTest;
import com.configly.toggle.write.application.port.in.ChangeFeatureToggleValueUseCase;
import com.configly.toggle.write.domain.featuretoggle.exception.CannotOperateOnArchivedFeatureToggleException;
import com.configly.toggle.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import com.configly.toggle.write.domain.reference.exception.CannotOperateOnArchivedEnvironmentException;
import com.configly.toggle.write.domain.reference.exception.CannotOperateOnArchivedProjectException;
import com.configly.toggle.write.domain.reference.exception.EnvironmentNotFoundException;
import com.configly.toggle.write.domain.reference.exception.ProjectNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static com.configly.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static com.configly.toggle.write.builder.FakeChangeFeatureToggleValueCommandBuilder.fakeChangeFeatureToggleValueCommandBuilder;
import static com.configly.toggle.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;

class ChangeFeatureToggleValueHandlerTest extends AbstractUnitTest {

    private ChangeFeatureToggleValueUseCase sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.changeFeatureToggleValueUseCase(
                featureToggleCommandRepositorySpy,
                featureToggleQueryRepositoryStub,
                projectRefConsistencySpy,
                environmentRefConsistencySpy,
                outboxWriter
        );
    }

    @Test
    void should_change_feature_toggle_value() {
        // given
        var expectedNewValue = FeatureToggleValueBuilder.bool(false);
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue(expectedNewValue.asText())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowReturns(
                fakeFeatureToggleBuilder()
                        .value(FeatureToggleValueBuilder.bool(true))
                        .build());

        // when
        sut.handle(command);

        // then
        var updated = featureToggleCommandRepositorySpy.lastUpdated();
        var value = updated.featureToggle().value();
        assertThat(value.asText()).isEqualTo(expectedNewValue.asText());
        assertThat(value.typeName()).isEqualTo(FeatureToggleValueType.BOOLEAN.name());
        assertContainsEventOfType(FEATURE_TOGGLE.topicName(), FeatureToggleValueChanged.class);
        var event = getLastPublishedEvent(FEATURE_TOGGLE.topicName(), FeatureToggleValueChanged.class);
        assertThat(event.value()).isEqualTo(expectedNewValue.asText());
    }

    @Test
    void should_do_nothing_if_value_not_changed() {
        // given
        var expectedNewValue = FeatureToggleValueBuilder.bool(true);
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue(expectedNewValue.asText())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowReturns(
                fakeFeatureToggleBuilder()
                        .value(expectedNewValue)
                        .build());
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        sut.handle(command);

        // then
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_throw_exception_when_change_value_and_feature_toggle_is_archived() {
        // given
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue("FALSE")
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);
        featureToggleQueryRepositoryStub.getOrThrowReturns(ARCHIVED_FEATURE_TOGGLE);
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.handle(command));

        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedFeatureToggleException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_throw_exception_when_change_value_and_project_is_archived() {
        // given
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue("FALSE")
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
    void should_throw_exception_when_change_value_and_environment_is_archived() {
        // given
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue("FALSE")
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
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue("FALSE")
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
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue("FALSE")
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
        var command = fakeChangeFeatureToggleValueCommandBuilder()
                .withNewValue("FALSE")
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
