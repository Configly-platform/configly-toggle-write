package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.shared.Changes;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.write.AbstractUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleField;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.shared.Changes.buildChange;

class FeatureToggleHandlerEventMapperTest extends AbstractUnitTest {

    @Test
    @DisplayName("Should map to featureToggleCreatedEvent")
    void test01() {
        // given
        var featureToggle = createFeatureToggle("TEST", ProjectId.create(), EnvironmentId.create());

        // when
        var featureToggleCreatedEvent = FeatureToggleHandlerEventMapper.createFeatureToggleCreatedEvent(featureToggle, actorProvider.current(), correlationProvider.current());

        // then
        assertThat(featureToggleCreatedEvent.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(featureToggleCreatedEvent.name()).isEqualTo(featureToggle.name().value());
        assertThat(featureToggleCreatedEvent.description()).isEqualTo(featureToggle.description().value());
        assertThat(featureToggleCreatedEvent.type()).isEqualTo(featureToggle.type().name());
        assertThat(featureToggleCreatedEvent.environmentId()).isEqualTo(featureToggle.environmentId().uuid());
        assertThat(featureToggleCreatedEvent.projectId()).isEqualTo(featureToggle.projectId().uuid());
        assertThat(featureToggleCreatedEvent.value()).isEqualTo(featureToggle.value().asText());
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
        var featureToggleDeletedEvent = FeatureToggleHandlerEventMapper.createFeatureToggleDeletedEvent(featureToggle.id(), actorProvider.current(), correlationProvider.current());

        // then
        assertThat(featureToggleDeletedEvent.id()).isEqualTo(featureToggle.id().uuid());
    }

    @Test
    @DisplayName("Should map to featureToggleUpdatedEvent")
    void test03() {
        // given
        var featureToggle = createFeatureToggle("TEST", ProjectId.create(), EnvironmentId.create());
        var updateResult = new FeatureToggleUpdateResult(featureToggle,
                List.of(
                        new FeatureToggleUpdateResult.FeatureToggleFieldChange(
                                FeatureToggleField.NAME,
                                FeatureToggleName.create("TEST"),
                                FeatureToggleName.create("TESTAFTER")
                        ))
        );
        var expectedChanges = Changes.of(buildChange(FeatureToggleField.NAME.name(), "TEST", "TESTAFTER"));

        // when
        var featureToggleUpdatedEvent = FeatureToggleHandlerEventMapper.createFeatureToggleUpdatedEvent(updateResult, actorProvider.current(), correlationProvider.current());

        // TODO Parametrized test for all switches in serialize method

        // then
        assertThat(featureToggleUpdatedEvent.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(featureToggleUpdatedEvent.name()).isEqualTo(featureToggle.name().value());
        assertThat(featureToggleUpdatedEvent.description()).isEqualTo(featureToggle.description().value());
        assertThat(featureToggleUpdatedEvent.type()).isEqualTo(featureToggle.type().name());
        assertThat(featureToggleUpdatedEvent.environmentId()).isEqualTo(featureToggle.environmentId().uuid());
        assertThat(featureToggleUpdatedEvent.projectId()).isEqualTo(featureToggle.projectId().uuid());
        assertThat(featureToggleUpdatedEvent.value()).isEqualTo(featureToggle.value().asText());
        assertThat(featureToggleUpdatedEvent.eventId()).isNotNull();
        assertThat(featureToggleUpdatedEvent.createdAt()).isNotNull();
        assertThat(featureToggleUpdatedEvent.updatedAt()).isNotNull();
        assertThat(featureToggleUpdatedEvent.changes()).isNotNull();
        assertThat(featureToggleUpdatedEvent.changes()).isEqualTo(expectedChanges);
    }

}