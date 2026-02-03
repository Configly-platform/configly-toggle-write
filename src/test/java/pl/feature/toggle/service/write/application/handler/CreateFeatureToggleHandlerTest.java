package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueType;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.reference.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static pl.feature.toggle.service.write.builder.FakeCreateFeatureToggleCommandBuilder.createFeatureToggleCommandBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;

class CreateFeatureToggleHandlerTest extends AbstractUnitTest {

    private CreateFeatureToggleUseCase sut;
    private ProjectSnapshot projectSnapshot;
    private EnvironmentSnapshot environmentSnapshot;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleRepository, projectRepository, environmentRepository, outboxWriter, actorProvider, correlationProvider);
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
                .withType(FeatureToggleValueType.BOOLEAN)
                .withValue("TRUE")
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
                .withType(FeatureToggleValueType.BOOLEAN)
                .withValue("TRUE")
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
                .withType(FeatureToggleValueType.BOOLEAN)
                .withValue("TRUE")
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
                .withType(FeatureToggleValueType.BOOLEAN)
                .withValue("TRUE")
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
                .withType(FeatureToggleValueType.BOOLEAN)
                .withValue("TRUE")
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
