package pl.feature.toggle.service.write.application.handler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.value.*;
import pl.feature.toggle.service.write.AbstractUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleField;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(featureToggleCreatedEvent.type()).isEqualTo(featureToggle.value().typeName());
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

    @ParameterizedTest(name = "{0} should be serialized correctly in changes")
    @MethodSource("serializeCases")
    @DisplayName("Should serialize before/after values for all FeatureToggleField cases")
    void should_serialize_all_fields_in_changes(
            FeatureToggleField field,
            Object before,
            Object after,
            String expectedBefore,
            String expectedAfter
    ) {
        // given
        var featureToggle = createFeatureToggle("TEST", ProjectId.create(), EnvironmentId.create());
        var updateResult = new FeatureToggleUpdateResult(
                featureToggle,
                List.of(new FeatureToggleUpdateResult.FeatureToggleFieldChange(field, before, after))
        );

        // when
        var event = FeatureToggleHandlerEventMapper.createFeatureToggleUpdatedEvent(
                updateResult,
                actorProvider.current(),
                correlationProvider.current()
        );

        // then
        assertThat(event.changes().changes()).hasSize(1);

        var change = event.changes().changes().getFirst();
        assertThat(change.field()).isEqualTo(field.name());
        assertThat(change.before()).isEqualTo(expectedBefore);
        assertThat(change.after()).isEqualTo(expectedAfter);
    }

    static Stream<Arguments> serializeCases() {
        var envIdBefore = EnvironmentId.create();
        var envIdAfter = EnvironmentId.create();

        var projectIdBefore = ProjectId.create();
        var projectIdAfter = ProjectId.create();

        var nameBefore = FeatureToggleName.create("TEST");
        var nameAfter = FeatureToggleName.create("TESTAFTER");

        var descBefore = FeatureToggleDescription.create("DESC");
        var descAfter = FeatureToggleDescription.create("DESCAFTER");

        var typeBefore = FeatureToggleType.BOOLEAN;
        var typeAfter = FeatureToggleType.TEXT;

        var valueBefore = FeatureToggleValueBuilder.bool(true);
        var valueAfter = FeatureToggleValueBuilder.text("abc");

        return Stream.of(
                Arguments.of(
                        FeatureToggleField.ENVIRONMENT_ID,
                        envIdBefore, envIdAfter,
                        envIdBefore.uuid().toString(), envIdAfter.uuid().toString()
                ),
                Arguments.of(
                        FeatureToggleField.PROJECT_ID,
                        projectIdBefore, projectIdAfter,
                        projectIdBefore.uuid().toString(), projectIdAfter.uuid().toString()
                ),
                Arguments.of(
                        FeatureToggleField.NAME,
                        nameBefore, nameAfter,
                        nameBefore.value(), nameAfter.value()
                ),
                Arguments.of(
                        FeatureToggleField.DESCRIPTION,
                        descBefore, descAfter,
                        descBefore.value(), descAfter.value()
                ),
                Arguments.of(
                        FeatureToggleField.TYPE,
                        typeBefore, typeAfter,
                        typeBefore.name(), typeAfter.name()
                ),
                Arguments.of(
                        FeatureToggleField.VALUE,
                        valueBefore, valueAfter,
                        valueBefore.asText(), valueAfter.asText()
                ),

                // + case na null (żeby pokryć if(value==null) return null)
                Arguments.of(
                        FeatureToggleField.NAME,
                        null, FeatureToggleName.create("X"),
                        null, "X"
                )
        );
    }

}