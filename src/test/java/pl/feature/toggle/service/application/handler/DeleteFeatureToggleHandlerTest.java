package pl.feature.toggle.service.application.handler;

import com.ftaas.contracts.event.featuretoggle.FeatureToggleDeleted;
import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.AbstractUnitTest;
import pl.feature.toggle.service.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.ftaas.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class DeleteFeatureToggleHandlerTest extends AbstractUnitTest {

    private DeleteFeatureToggleUseCase sut;
    private ProjectSnapshot projectSnapshot;
    private EnvironmentSnapshot environmentSnapshot;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.deleteFeatureToggleUseCase(featureToggleRepository, outboxWriter);
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
