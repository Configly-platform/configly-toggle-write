package pl.feature.toggle.service.write.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.topic.KafkaTopic;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedEnvironmentException;
import pl.feature.toggle.service.write.domain.reference.exception.CannotOperateOnArchivedProjectException;
import pl.feature.toggle.service.write.domain.reference.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.write.builder.FakeCreateFeatureToggleCommandBuilder.fakeCreateFeatureToggleCommandBuilder;

class CreateFeatureToggleHandlerTest extends AbstractUnitTest {

    private CreateFeatureToggleUseCase sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.createFeatureToggleUseCase(
                featureToggleCommandRepositorySpy,
                projectRefConsistencySpy,
                environmentRefConsistencySpy,
                outboxWriter
        );
    }

    @Test
    void should_save_feature_toggle() {
        // given
        var command = fakeCreateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ACTIVE_PROJECT);


        // when
        sut.execute(command);

        // then
        var actual = featureToggleCommandRepositorySpy.lastSaved();
        assertThat(actual.status()).isEqualTo(FeatureToggleStatus.ACTIVE);
        assertThat(actual.id()).isNotNull();
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.updatedAt()).isNotNull();
        assertThat(actual.name()).isEqualTo(command.name());
        assertThat(actual.description()).isEqualTo(command.description());
        assertThat(actual.environmentId()).isEqualTo(command.environmentId());
        assertThat(actual.revision()).isEqualTo(Revision.initialRevision());
        assertContainsEventOfType(KafkaTopic.FEATURE_TOGGLE.topic(), FeatureToggleCreated.class);
    }

    @Test
    void should_not_save_feature_toggle_when_project_is_archived() {
        // given
        var command = fakeCreateFeatureToggleCommandBuilder()
                .withEnvironmentId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedReturns(ARCHIVED_PROJECT);
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));


        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedProjectException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_save_feature_toggle_when_environment_is_archived() {
        // given
        var command = fakeCreateFeatureToggleCommandBuilder()
                .withEnvironmentId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));


        // then
        assertThat(exception).isInstanceOf(CannotOperateOnArchivedEnvironmentException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_save_feature_toggle_when_project_doesnt_exist() {
        // given
        var command = fakeCreateFeatureToggleCommandBuilder()
                .withEnvironmentId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedReturns(ACTIVE_ENVIRONMENT_WITH_ACTIVE_PROJECT);
        projectRefConsistencySpy.getTrustedThrows(new ProjectNotFoundException(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId()));
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));


        // then
        assertThat(exception).isInstanceOf(ProjectNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }

    @Test
    void should_not_save_feature_toggle_when_environment_doesnt_exist() {
        // given
        var command = fakeCreateFeatureToggleCommandBuilder()
                .withEnvironmentId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId())
                .withProjectId(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId())
                .build();
        environmentRefConsistencySpy.getTrustedThrows(new EnvironmentNotFoundException(ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.projectId(),
                ARCHIVED_ENVIRONMENT_WITH_ACTIVE_PROJECT.environmentId()));
        projectRefConsistencySpy.expectNoCalls();
        featureToggleCommandRepositorySpy.expectNoCalls();

        // when
        var exception = catchException(() -> sut.execute(command));


        // then
        assertThat(exception).isInstanceOf(EnvironmentNotFoundException.class);
        assertNoEventsHasBeenPublished();
    }

}
