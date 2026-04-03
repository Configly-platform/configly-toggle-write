package com.configly.toggle.write.application.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.configly.outbox.api.OutboxEvent;
import com.configly.web.actor.Actor;
import com.configly.web.correlation.CorrelationId;
import com.configly.outbox.api.OutboxWriter;
import com.configly.toggle.write.application.port.in.ArchiveFeatureTogglesByEnvironmentUseCase;
import com.configly.toggle.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;
import com.configly.toggle.write.application.port.out.FeatureToggleCommandRepository;
import com.configly.toggle.write.application.port.out.FeatureToggleQueryRepository;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggleUpdateResult;

import static com.configly.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static com.configly.toggle.write.application.handler.EventMapper.createFeatureToggleStatusChangedEvent;

@AllArgsConstructor
@Slf4j
class ArchiveFeatureTogglesByEnvironmentHandler implements ArchiveFeatureTogglesByEnvironmentUseCase {

    private final FeatureToggleQueryRepository featureToggleQueryRepository;
    private final FeatureToggleCommandRepository featureToggleCommandRepository;
    private final OutboxWriter outboxWriter;

    @Override
    @Transactional
    public void handle(ArchiveFeatureTogglesByEnvironmentCommand command) {
        log.info("Archiving feature toggles by environment: environmentId={}", command.environmentId().uuid());
        var toggles = featureToggleQueryRepository.findByEnvironmentId(command.environmentId());
        int archivedCount = 0;

        for (var toggle : toggles) {
            var result = toggle.archive();
            if (!result.wasUpdated()) {
                continue;
            }

            featureToggleCommandRepository.update(result);
            sendFeatureToggleStatusChangedEvent(result, command.actor(), command.correlationId());
            archivedCount++;
        }

        log.info(
                "Feature toggles archived by environment: environmentId={}, archivedCount={}",
                command.environmentId().uuid(),
                archivedCount
        );

    }

    private void sendFeatureToggleStatusChangedEvent(FeatureToggleUpdateResult result, Actor actor, CorrelationId correlationId) {
        var event = createFeatureToggleStatusChangedEvent(result, actor, correlationId);
        outboxWriter.write(OutboxEvent.generatedKey(event, FEATURE_TOGGLE));
    }


}
