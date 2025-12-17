package pl.feature.toggle.service.application.handler;

import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.project.ProjectId;
import pl.feature.toggle.service.AbstractUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleHandlerEventMapperTest extends AbstractUnitTest {

    @Test
    @DisplayName("Should map to featureToggleCreatedEvent")
    void test01() {
        // given
        var featureToggle = createFeatureToggle("TEST", ProjectId.create(), EnvironmentId.create());

        // when
        var featureToggleCreatedEvent = FeatureToggleHandlerEventMapper.createFeatureToggleCreatedEvent(featureToggle);

        // then
        assertThat(featureToggleCreatedEvent.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(featureToggleCreatedEvent.name()).isEqualTo(featureToggle.name().value());
        assertThat(featureToggleCreatedEvent.description()).isEqualTo(featureToggle.description().value());
        assertThat(featureToggleCreatedEvent.type()).isEqualTo(featureToggle.type().name());
        assertThat(featureToggleCreatedEvent.environmentId()).isEqualTo(featureToggle.environmentId().uuid());
        assertThat(featureToggleCreatedEvent.projectId()).isEqualTo(featureToggle.projectId().uuid());
        assertThat(featureToggleCreatedEvent.value()).isEqualTo(featureToggle.value().stringValue());
        assertThat(featureToggleCreatedEvent.eventId()).isNotNull();
        assertThat(featureToggleCreatedEvent.createdAt()).isNotNull();
        assertThat(featureToggleCreatedEvent.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should map to featureToggleDeletedEvent")
    void test02() {
        // given
        var featureToggle = createFeatureToggle("TEST", ProjectId.create(), EnvironmentId.create());

        // when
        var featureToggleDeletedEvent = FeatureToggleHandlerEventMapper.createFeatureToggleDeletedEvent(featureToggle.id());

        // then
        assertThat(featureToggleDeletedEvent.id()).isEqualTo(featureToggle.id().uuid());
    }

    @Test
    @DisplayName("Should map to featureToggleUpdatedEvent")
    void test03() {
        // given
        var featureToggle = createFeatureToggle("TEST", ProjectId.create(), EnvironmentId.create());

        // when
        var featureToggleUpdatedEvent = FeatureToggleHandlerEventMapper.createFeatureToggleUpdatedEvent(featureToggle);

        // then
        assertThat(featureToggleUpdatedEvent.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(featureToggleUpdatedEvent.name()).isEqualTo(featureToggle.name().value());
        assertThat(featureToggleUpdatedEvent.description()).isEqualTo(featureToggle.description().value());
        assertThat(featureToggleUpdatedEvent.type()).isEqualTo(featureToggle.type().name());
        assertThat(featureToggleUpdatedEvent.environmentId()).isEqualTo(featureToggle.environmentId().uuid());
        assertThat(featureToggleUpdatedEvent.projectId()).isEqualTo(featureToggle.projectId().uuid());
        assertThat(featureToggleUpdatedEvent.value()).isEqualTo(featureToggle.value().stringValue());
        assertThat(featureToggleUpdatedEvent.eventId()).isNotNull();
        assertThat(featureToggleUpdatedEvent.createdAt()).isNotNull();
        assertThat(featureToggleUpdatedEvent.updatedAt()).isNotNull();
    }

}