package pl.feature.toggle.service.write.application.handler;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.security.actor.Actor;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.port.in.ArchiveFeatureTogglesByEnvironmentUseCase;
import pl.feature.toggle.service.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleStatusChangedEvent;

@AllArgsConstructor
class ArchiveFeatureTogglesByEnvironmentHandler implements ArchiveFeatureTogglesByEnvironmentUseCase {

    private final FeatureToggleQueryRepository featureToggleQueryRepository;
    private final FeatureToggleCommandRepository featureToggleCommandRepository;
    private final OutboxWriter outboxWriter;

    @Override
    @Transactional
    public void handle(ArchiveFeatureTogglesByEnvironmentCommand command) {
        var toggles = featureToggleQueryRepository.findByEnvironmentId(command.environmentId());

        for (var toggle : toggles) {
            var result = toggle.archive();
            if (!result.wasUpdated()) {
                continue;
            }

            featureToggleCommandRepository.update(result);
            sendFeatureToggleStatusChangedEvent(result, command.actor(), command.correlationId());
        }

    }

    private void sendFeatureToggleStatusChangedEvent(FeatureToggleUpdateResult result, Actor actor, CorrelationId correlationId) {
        var event = createFeatureToggleStatusChangedEvent(result, actor, correlationId);
        outboxWriter.write(event, FEATURE_TOGGLE.topic());
    }


}
