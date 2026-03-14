package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged;
import pl.feature.toggle.service.contracts.shared.Changes;
import pl.feature.toggle.service.contracts.shared.Metadata;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.model.security.actor.Actor;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.value.FeatureToggleValue;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleField;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleStatusChanged.featureToggleStatusChangedBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated.featureToggleUpdatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleValueChanged.featureToggleValueChangedBuilder;

final class EventMapper {

    static FeatureToggleCreated createFeatureToggleCreatedEvent(FeatureToggle featureToggle,
                                                                Actor actor,
                                                                CorrelationId correlationId) {
        return featureToggleCreatedEventBuilder()
                .id(featureToggle.id().uuid())
                .createdAt(featureToggle.createdAt().toLocalDateTime())
                .updatedAt(featureToggle.updatedAt().toLocalDateTime())
                .type(featureToggle.value().typeName())
                .value(featureToggle.value().asText())
                .name(featureToggle.name().value())
                .revision(featureToggle.revision().value())
                .status(featureToggle.status().name())
                .description(featureToggle.description().value())
                .environmentId(featureToggle.environmentId().uuid())
                .metadata(Metadata.create(actor.idAsString(), actor.usernameAsString(), correlationId.value()))
                .build();
    }

    static FeatureToggleStatusChanged createFeatureToggleStatusChangedEvent(FeatureToggleUpdateResult updateResult,
                                                                            Actor actor,
                                                                            CorrelationId correlationId) {
        var featureToggle = updateResult.featureToggle();
        return featureToggleStatusChangedBuilder()
                .id(featureToggle.id().uuid())
                .status(featureToggle.status().name())
                .revision(featureToggle.revision().value())
                .environmentId(featureToggle.environmentId().uuid())
                .metadata(Metadata.create(actor.idAsString(), actor.usernameAsString(), correlationId.value()))
                .changes(buildChanges(updateResult))
                .build();
    }

    static FeatureToggleValueChanged createFeatureToggleValueChangedEvent(FeatureToggleUpdateResult updateResult,
                                                                          Actor actor,
                                                                          CorrelationId correlationId) {
        var featureToggle = updateResult.featureToggle();
        return featureToggleValueChangedBuilder()
                .id(featureToggle.id().uuid())
                .type(featureToggle.value().typeName())
                .revision(featureToggle.revision().value())
                .value(featureToggle.value().asText())
                .environmentId(updateResult.featureToggle().environmentId().uuid())
                .changes(buildChanges(updateResult))
                .metadata(Metadata.create(actor.idAsString(), actor.usernameAsString(), correlationId.value()))
                .build();
    }


    static FeatureToggleUpdated createFeatureToggleUpdatedEvent(FeatureToggleUpdateResult updateResult,
                                                                Actor actor,
                                                                CorrelationId correlationId) {
        var featureToggle = updateResult.featureToggle();
        var changes = buildChanges(updateResult);

        return featureToggleUpdatedEventBuilder()
                .id(featureToggle.id().uuid())
                .revision(featureToggle.revision().value())
                .createdAt(featureToggle.createdAt().toLocalDateTime())
                .updatedAt(featureToggle.updatedAt().toLocalDateTime())
                .name(featureToggle.name().value())
                .description(featureToggle.description().value())
                .environmentId(featureToggle.environmentId().uuid())
                .metadata(Metadata.create(actor.idAsString(), actor.usernameAsString(), correlationId.value()))
                .changes(changes)
                .build();
    }

    private static Changes buildChanges(FeatureToggleUpdateResult updateResult) {
        return new Changes(
                updateResult.changes().stream()
                        .map(it -> Changes.buildChange(
                                it.field().name(),
                                serialize(it.field(), it.before()),
                                serialize(it.field(), it.after())
                        ))
                        .toList()
        );
    }

    private static String serialize(FeatureToggleField field, Object value) {
        if (value == null) {
            return null;
        }

        return switch (field) {
            case ENVIRONMENT_ID -> ((EnvironmentId) value).uuid().toString();
            case NAME -> ((FeatureToggleName) value).value();
            case DESCRIPTION -> ((FeatureToggleDescription) value).value();
            case VALUE -> ((FeatureToggleValue) value).asText();
            case STATUS -> ((FeatureToggleStatus) value).name();
        };
    }

}
