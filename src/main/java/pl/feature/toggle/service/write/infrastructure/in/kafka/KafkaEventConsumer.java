package pl.feature.toggle.service.write.infrastructure.in.kafka;

import org.springframework.kafka.support.Acknowledgment;
import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;
import pl.feature.toggle.service.contracts.shared.EventProcessor;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = "${topics.project-env-events}")
class KafkaEventConsumer {

    private final ProjectEnvironmentProjection projectionUseCase;
    private final EventProcessor eventProcessor;

    @KafkaHandler
    void handle(ProjectCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectionUseCase::handle,
                acknowledgment::acknowledge
        );
    }

    @KafkaHandler
    void handle(EnvironmentCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectionUseCase::handle,
                acknowledgment::acknowledge
        );
    }

}
