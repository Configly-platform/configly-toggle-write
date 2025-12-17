package pl.feature.toggle.service.application.handler;

import com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleDeleted;
import com.ftaas.contracts.event.featuretoggle.FeatureToggleUpdated;
import com.ftaas.domain.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;

import static com.ftaas.contracts.event.featuretoggle.FeatureToggleCreated.featureToggleCreatedEventBuilder;
import static com.ftaas.contracts.event.featuretoggle.FeatureToggleDeleted.featureToggleDeletedEvent;
import static com.ftaas.contracts.event.featuretoggle.FeatureToggleUpdated.featureToggleUpdatedEventBuilder;

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
