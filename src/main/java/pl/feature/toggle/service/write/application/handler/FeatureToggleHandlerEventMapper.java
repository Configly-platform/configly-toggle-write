package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted;
import pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleDeleted.featureToggleDeletedEvent;
import static pl.feature.toggle.service.contracts.event.featuretoggle.FeatureToggleUpdated.featureToggleUpdatedEventBuilder;


final class FeatureToggleHandlerEventMapper {

    static FeatureToggleCreated createFeatureToggleCreatedEvent(FeatureToggle featureToggle) {
        return featureToggleCreatedEventBuilder()
                .id(featureToggle.id().uuid())
                .createdAt(featureToggle.createdAt().toLocalDateTime())
                .updatedAt(featureToggle.updatedAt().toLocalDateTime())
                .type(featureToggle.type().name())
                .value(featureToggle.value().stringValue())
                .name(featureToggle.name().value())
                .description(featureToggle.description().value())
                .environmentId(featureToggle.environmentId().uuid())
                .projectId(featureToggle.projectId().uuid())
                .build();
    }

    static FeatureToggleDeleted createFeatureToggleDeletedEvent(FeatureToggleId featureToggleId) {
        return featureToggleDeletedEvent()
                .id(featureToggleId.uuid())
                .build();
    }

    static FeatureToggleUpdated createFeatureToggleUpdatedEvent(FeatureToggle featureToggle) {
        return featureToggleUpdatedEventBuilder()
                .id(featureToggle.id().uuid())
                .createdAt(featureToggle.createdAt().toLocalDateTime())
                .updatedAt(featureToggle.updatedAt().toLocalDateTime())
                .type(featureToggle.type().name())
                .value(featureToggle.value().stringValue())
                .name(featureToggle.name().value())
                .description(featureToggle.description().value())
                .environmentId(featureToggle.environmentId().uuid())
                .projectId(featureToggle.projectId().uuid())
                .build();
    }

}
