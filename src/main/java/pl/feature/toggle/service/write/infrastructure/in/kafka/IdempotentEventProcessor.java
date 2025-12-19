package pl.feature.toggle.service.write.infrastructure.in.kafka;

import pl.feature.toggle.service.write.application.port.out.ProcessedEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.contracts.shared.EventProcessor;
import pl.feature.toggle.service.contracts.shared.IntegrationEvent;

import java.util.function.Consumer;

@AllArgsConstructor
@Slf4j
class IdempotentEventProcessor implements EventProcessor {

    private final ProcessedEventRepository processedEvents;

    @Override
    @Transactional
    public <T extends IntegrationEvent> void process(T event, Consumer<T> action) {
        logReceiveEvent(event);

        if (processedEvents.alreadyProcessed(event.eventId())) {
            logEventAlreadyProcessed(event);
            return;
        }

        action.accept(event);

        processedEvents.markProcessed(event.eventId());
        logProcessedEvent(event);
    }

    private void logReceiveEvent(IntegrationEvent event) {
        log.info("Received integration event: {}", event);
    }

    private void logEventAlreadyProcessed(IntegrationEvent event) {
        log.info("Integration event {} already processed – skipping", event.eventId());
    }

    private void logProcessedEvent(IntegrationEvent event) {
        log.info("Integration event {} processed", event.eventId());
    }
}
