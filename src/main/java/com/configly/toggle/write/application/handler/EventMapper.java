package com.configly.toggle.write.application.handler;

import com.configly.contracts.event.featuretoggle.FeatureToggleCreated;
import com.configly.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import com.configly.contracts.event.featuretoggle.FeatureToggleUpdated;
import com.configly.contracts.event.featuretoggle.FeatureToggleValueChanged;
import com.configly.contracts.shared.Changes;
import com.configly.contracts.shared.Metadata;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.value.FeatureToggleValue;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggle;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggleField;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult;

import static com.configly.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static com.configly.contracts.event.featuretoggle.FeatureToggleStatusChanged.featureToggleStatusChangedBuilder;
import static com.configly.contracts.event.featuretoggle.FeatureToggleUpdated.featureToggleUpdatedEventBuilder;
import static com.configly.contracts.event.featuretoggle.FeatureToggleValueChanged.featureToggleValueChangedBuilder;

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
