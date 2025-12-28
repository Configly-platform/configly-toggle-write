package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;

class DeleteFeatureToggleHandlerTest extends AbstractUnitTest {

    private DeleteFeatureToggleUseCase sut;
    private ProjectSnapshot projectSnapshot;
    private EnvironmentSnapshot environmentSnapshot;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.deleteFeatureToggleUseCase(featureToggleRepository, outboxWriter, actorProvider, correlationProvider);
        projectSnapshot = createProject();
        environmentSnapshot = createEnvironment(projectSnapshot.id());
    }

    @Test
    @DisplayName("Should delete existing feature toggle")
    void test01() {
        // given
        var featureToggle = createFeatureToggle("TEST", projectSnapshot.id(), environmentSnapshot.id());
        var toggleExists = featureToggleRepository.exists(featureToggle.id());
        assertThat(toggleExists).isTrue();

        // when
        sut.execute(featureToggle.id());

        // then
        assertThat(featureToggleRepository.exists(featureToggle.id())).isFalse();
        assertContainsFeatureToggleDeletedEvent();
    }

    @Test
    @DisplayName("Should throw exception when feature toggle doesn't exist")
    void test02() {
        // given && when
        var exception = catchException(() -> sut.execute(FeatureToggleId.create()));

        // then
        assertThat(exception).isInstanceOf(FeatureToggleNotFoundException.class);
        assertDoesNotContainFeatureToggleDeletedEvent();
    }

    private void assertContainsFeatureToggleDeletedEvent() {
        assertThat(outboxWriter.containsEventOfType(FEATURE_TOGGLE.topic(), FeatureToggleDeleted.class)).isTrue();
    }

    private void assertDoesNotContainFeatureToggleDeletedEvent() {
        assertThat(outboxWriter.containsEventOfType(FEATURE_TOGGLE.topic(), FeatureToggleDeleted.class)).isFalse();
    }

}
