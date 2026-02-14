package pl.feature.toggle.service.write.application.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleField;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.write.builder.FakeEnvironmentRefBuilder.fakeEnvironmentRefBuilder;
import static pl.feature.toggle.service.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;

class EventMapperTest extends AbstractUnitTest {

    @Test
    void should_map_to_feature_toggle_created_event() {
        // given
        var featureToggle = fakeFeatureToggleBuilder().build();
        var environmentRef = fakeEnvironmentRefBuilder().build();

        // when
        var featureToggleCreatedEvent = EventMapper.createFeatureToggleCreatedEvent(featureToggle, environmentRef, actorProvider.current(), correlationProvider.current());

        // then
        assertThat(featureToggleCreatedEvent.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(featureToggleCreatedEvent.name()).isEqualTo(featureToggle.name().value());
        assertThat(featureToggleCreatedEvent.description()).isEqualTo(featureToggle.description().value());
        assertThat(featureToggleCreatedEvent.type()).isEqualTo(featureToggle.value().typeName());
        assertThat(featureToggleCreatedEvent.environmentId()).isEqualTo(featureToggle.environmentId().uuid());
        assertThat(featureToggleCreatedEvent.projectId()).isEqualTo(environmentRef.projectId().uuid());
        assertThat(featureToggleCreatedEvent.value()).isEqualTo(featureToggle.value().asText());
        assertThat(featureToggleCreatedEvent.eventId()).isNotNull();
        assertThat(featureToggleCreatedEvent.createdAt()).isNotNull();
        assertThat(featureToggleCreatedEvent.updatedAt()).isNotNull();
    }

    @Test
    void should_map_to_feature_toggle_updated_event() {
        // given
        var featureToggle = fakeFeatureToggleBuilder().build();
        var environmentRef = fakeEnvironmentRefBuilder().build();

        var updateResult = new FeatureToggleUpdateResult(
                featureToggle,
                Revision.initialRevision(),
                List.of(new FeatureToggleUpdateResult.FeatureToggleFieldChange(
                        FeatureToggleField.NAME,
                        FeatureToggleName.create("BEFORE"),
                        FeatureToggleName.create("AFTER")
                ))
        );

        // when
        var event = EventMapper.createFeatureToggleUpdatedEvent(
                updateResult,
                environmentRef,
                actorProvider.current(),
                correlationProvider.current()
        );

        // then
        assertThat(event.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(event.name()).isEqualTo(featureToggle.name().value());
        assertThat(event.description()).isEqualTo(featureToggle.description().value());
        assertThat(event.environmentId()).isEqualTo(featureToggle.environmentId().uuid());
        assertThat(event.projectId()).isEqualTo(environmentRef.projectId().uuid());
        assertThat(event.createdAt()).isEqualTo(featureToggle.createdAt().toLocalDateTime());
        assertThat(event.updatedAt()).isEqualTo(featureToggle.updatedAt().toLocalDateTime());
        assertThat(event.eventId()).isNotNull();

        assertThat(event.changes().changes()).hasSize(1);
        var change = event.changes().changes().getFirst();
        assertThat(change.field()).isEqualTo(FeatureToggleField.NAME.name());
        assertThat(change.before()).isEqualTo("BEFORE");
        assertThat(change.after()).isEqualTo("AFTER");
    }

    @Test
    void should_map_to_feature_toggle_status_changed_event() {
        // given
        var featureToggle = fakeFeatureToggleBuilder().build();
        var environmentRef = fakeEnvironmentRefBuilder().build();

        var updateResult = new FeatureToggleUpdateResult(
                featureToggle,
                Revision.initialRevision(),
                List.of(new FeatureToggleUpdateResult.FeatureToggleFieldChange(
                        FeatureToggleField.STATUS,
                        FeatureToggleStatus.ACTIVE,
                        FeatureToggleStatus.ARCHIVED
                ))
        );

        // when
        var event = EventMapper.createFeatureToggleStatusChangedEvent(
                updateResult,
                environmentRef,
                actorProvider.current(),
                correlationProvider.current()
        );

        // then
        assertThat(event.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(event.status()).isEqualTo(featureToggle.status().name());
        assertThat(event.environmentId()).isEqualTo(featureToggle.environmentId().uuid());
        assertThat(event.projectId()).isEqualTo(environmentRef.projectId().uuid());
        assertThat(event.eventId()).isNotNull();

        assertThat(event.changes().changes()).hasSize(1);
        var change = event.changes().changes().getFirst();
        assertThat(change.field()).isEqualTo(FeatureToggleField.STATUS.name());
        assertThat(change.before()).isEqualTo(FeatureToggleStatus.ACTIVE.name());
        assertThat(change.after()).isEqualTo(FeatureToggleStatus.ARCHIVED.name());
    }

    @Test
    void should_map_to_feature_toggle_value_changed_event() {
        // given
        var featureToggle = fakeFeatureToggleBuilder().build();
        var environmentRef = fakeEnvironmentRefBuilder().build();

        var before = FeatureToggleValueBuilder.bool(true);
        var after = FeatureToggleValueBuilder.text("abc");

        var updateResult = new FeatureToggleUpdateResult(
                featureToggle,
                Revision.initialRevision(),
                List.of(new FeatureToggleUpdateResult.FeatureToggleFieldChange(
                        FeatureToggleField.VALUE,
                        before,
                        after
                ))
        );

        // when
        var event = EventMapper.createFeatureToggleValueChangedEvent(
                updateResult,
                environmentRef,
                actorProvider.current(),
                correlationProvider.current()
        );

        // then
        assertThat(event.id()).isEqualTo(featureToggle.id().uuid());
        assertThat(event.type()).isEqualTo(featureToggle.value().typeName());
        assertThat(event.value()).isEqualTo(featureToggle.value().asText());
        assertThat(event.projectId()).isEqualTo(environmentRef.projectId().uuid());

        assertThat(event.environmentId()).isEqualTo(environmentRef.environmentId().uuid());
        assertThat(event.eventId()).isNotNull();

        assertThat(event.changes().changes()).hasSize(1);
        var change = event.changes().changes().getFirst();
        assertThat(change.field()).isEqualTo(FeatureToggleField.VALUE.name());
        assertThat(change.before()).isEqualTo(before.asText());
        assertThat(change.after()).isEqualTo(after.asText());
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
        var featureToggle = fakeFeatureToggleBuilder().build();
        var environmentRef = fakeEnvironmentRefBuilder().build();
        var updateResult = new FeatureToggleUpdateResult(
                featureToggle,
                Revision.initialRevision(),
                List.of(new FeatureToggleUpdateResult.FeatureToggleFieldChange(field, before, after))
        );

        // when
        var event = EventMapper.createFeatureToggleUpdatedEvent(
                updateResult,
                environmentRef,
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

        var nameBefore = FeatureToggleName.create("TEST");
        var nameAfter = FeatureToggleName.create("TESTAFTER");

        var descBefore = FeatureToggleDescription.create("DESC");
        var descAfter = FeatureToggleDescription.create("DESCAFTER");

        var valueBefore = FeatureToggleValueBuilder.bool(true);
        var valueAfter = FeatureToggleValueBuilder.text("abc");

        return Stream.of(
                Arguments.of(
                        FeatureToggleField.ENVIRONMENT_ID,
                        envIdBefore, envIdAfter,
                        envIdBefore.uuid().toString(), envIdAfter.uuid().toString()
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
                        FeatureToggleField.VALUE,
                        valueBefore, valueAfter,
                        valueBefore.asText(), valueAfter.asText()
                ),

                Arguments.of(
                        FeatureToggleField.NAME,
                        null, FeatureToggleName.create("X"),
                        null, "X"
                )
        );
    }

}