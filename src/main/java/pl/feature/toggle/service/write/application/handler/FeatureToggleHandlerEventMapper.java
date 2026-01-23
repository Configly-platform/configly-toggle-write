package pl.feature.toggle.service.write.application.handler;

import org.jspecify.annotations.NonNull;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.contracts.shared.Changes;
import pl.feature.toggle.service.contracts.shared.Metadata;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValue;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.security.actor.Actor;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleField;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted.featureToggleDeletedEvent;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated.featureToggleUpdatedEventBuilder;
import static pl.feature.toggle.service.contracts.shared.Changes.buildChange;


final class FeatureToggleHandlerEventMapper {

    static FeatureToggleCreated createFeatureToggleCreatedEvent(FeatureToggle featureToggle, Actor actor, CorrelationId correlationId) {
        return featureToggleCreatedEventBuilder()
                .id(featureToggle.id().uuid())
                .createdAt(featureToggle.createdAt().toLocalDateTime())
                .updatedAt(featureToggle.updatedAt().toLocalDateTime())
                .type(featureToggle.value().typeName())
                .value(featureToggle.value().asText())
                .name(featureToggle.name().value())
                .description(featureToggle.description().value())
                .environmentId(featureToggle.environmentId().uuid())
                .projectId(featureToggle.projectId().uuid())
                .metadata(Metadata.create(actor.idAsString(), actor.usernameAsString(), correlationId.value()))
                .build();
    }

    static FeatureToggleDeleted createFeatureToggleDeletedEvent(FeatureToggleId featureToggleId, Actor actor, CorrelationId correlationId) {
        return featureToggleDeletedEvent()
                .id(featureToggleId.uuid())
                .metadata(Metadata.create(actor.idAsString(), actor.usernameAsString(), correlationId.value()))
                .build();
    }

    static FeatureToggleUpdated createFeatureToggleUpdatedEvent(FeatureToggleUpdateResult updateResult, Actor actor, CorrelationId correlationId) {
        var featureToggle = updateResult.updated();
        var changes = buildChanges(updateResult);

        return featureToggleUpdatedEventBuilder()
                .id(featureToggle.id().uuid())
                .createdAt(featureToggle.createdAt().toLocalDateTime())
                .updatedAt(featureToggle.updatedAt().toLocalDateTime())
                .type(featureToggle.value().typeName())
                .value(featureToggle.value().asText())
                .name(featureToggle.name().value())
                .description(featureToggle.description().value())
                .environmentId(featureToggle.environmentId().uuid())
                .projectId(featureToggle.projectId().uuid())
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
            case PROJECT_ID -> ((ProjectId) value).uuid().toString();
            case NAME -> ((FeatureToggleName) value).value();
            case DESCRIPTION -> ((FeatureToggleDescription) value).value();
            case TYPE -> ((FeatureToggleType) value).name();
            case VALUE -> ((FeatureToggleValue) value).asText();
        };
    }

}
