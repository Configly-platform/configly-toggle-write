package pl.feature.toggle.service.write.application.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.web.actor.Actor;
import pl.feature.toggle.service.web.correlation.CorrelationId;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.write.application.port.in.ArchiveFeatureTogglesByEnvironmentUseCase;
import pl.feature.toggle.service.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;

import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.application.handler.EventMapper.createFeatureToggleStatusChangedEvent;

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
        outboxWriter.write(event, FEATURE_TOGGLE.topic());
    }


}
