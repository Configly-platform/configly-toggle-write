package pl.feature.toggle.service.application.handler;

import pl.feature.toggle.service.AbstractUnitTest;
import pl.feature.toggle.service.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.environment.exception.EnvironmentNotAssignedToProjectException;
import pl.feature.toggle.service.domain.environment.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.domain.project.exception.ProjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleType;

import java.util.UUID;

import static pl.feature.toggle.service.builder.FakeCreateFeatureToggleCommandBuilder.createFeatureToggleCommandBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;

class CreateFeatureToggleHandlerTest extends AbstractUnitTest {

    private CreateFeatureToggleUseCase sut;
    private ProjectSnapshot projectSnapshot;
    private EnvironmentSnapshot environmentSnapshot;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleRepository, projectRepository, environmentRepository, outboxWriter);
        projectSnapshot = createProject();
        environmentSnapshot = createEnvironment(projectSnapshot.id());
    }


    @Test
    @DisplayName("Should create a new feature toggle")
    void test01() {
        // given
        var command = createFeatureToggleCommandBuilder()
                .withDescription("TEST")
                .withName("FEATURE_TOGGLE_01")
                .withType(FeatureToggleType.BOOLEAN)
                .withValue("true")
                .withEnvironmentId(environmentSnapshot.id().idAsString())
                .withProjectId(projectSnapshot.id().idAsString())
                .build();

        // when
        var result = sut.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(featureToggleRepository.exists(result)).isTrue();
        assertContainsFeatureToggleCreatedEvent();
    }

    @Test
    @DisplayName("Should not create a new feature toggle when toggle with given name already exists")
    void test02() {
        // given
        createFeatureToggle("TEST", projectSnapshot.id(), environmentSnapshot.id());
        var command = createFeatureToggleCommandBuilder()
                .withDescription("TEST")
                .withName("TEST")
                .withType(FeatureToggleType.BOOLEAN)
                .withValue("true")
                .withEnvironmentId(environmentSnapshot.id().idAsString())
                .withProjectId(projectSnapshot.id().idAsString())
                .build();

        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(FeatureToggleAlreadyExistsException.class);
        assertDoesNotContainFeatureToggleCreatedEvent();
    }

    @Test
    @DisplayName("Should not create a new feature toggle when project doesn't exist")
    void test03() {
        var command = createFeatureToggleCommandBuilder()
                .withDescription("TEST")
                .withName("TEST")
                .withType(FeatureToggleType.BOOLEAN)
                .withValue("true")
                .withEnvironmentId(environmentSnapshot.id().idAsString())
                .withProjectId(UUID.randomUUID().toString())
                .build();

        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(ProjectNotFoundException.class);
        assertDoesNotContainFeatureToggleCreatedEvent();
    }

    @Test
    @DisplayName("Should not create a new feature toggle when environment doesn't exist")
    void test04() {
        var command = createFeatureToggleCommandBuilder()
                .withDescription("TEST")
                .withName("TEST")
                .withType(FeatureToggleType.BOOLEAN)
                .withValue("true")
                .withEnvironmentId(UUID.randomUUID().toString())
                .withProjectId(projectSnapshot.id().idAsString())
                .build();

        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(EnvironmentNotFoundException.class);
        assertDoesNotContainFeatureToggleCreatedEvent();
    }

    @Test
    @DisplayName("Should not create a new feature toggle when environment does not belong to project")
    void test05() {
        var tempProject = createProject();
        var environment = createEnvironment(tempProject.id());
        var command = createFeatureToggleCommandBuilder()
                .withDescription("TEST")
                .withName("TEST")
                .withType(FeatureToggleType.BOOLEAN)
                .withValue("true")
                .withEnvironmentId(environment.id().idAsString())
                .withProjectId(projectSnapshot.id().idAsString())
                .build();

        // when
        var exception = catchException(() -> sut.execute(command));

        // then
        assertThat(exception).isInstanceOf(EnvironmentNotAssignedToProjectException.class);
        assertDoesNotContainFeatureToggleCreatedEvent();
    }

    private void assertContainsFeatureToggleCreatedEvent() {
        assertThat(outboxWriter.containsEventOfType(FEATURE_TOGGLE.topic(), FeatureToggleCreated.class)).isTrue();
    }

    private void assertDoesNotContainFeatureToggleCreatedEvent() {
        assertThat(outboxWriter.containsEventOfType(FEATURE_TOGGLE.topic(), FeatureToggleCreated.class)).isFalse();
    }
}
