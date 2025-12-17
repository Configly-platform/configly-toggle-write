package pl.feature.toggle.service.infrastructure.in.kafka;

import pl.feature.toggle.service.application.port.in.ProjectEnvironmentProjectionUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import pl.feature.toggle.service.contracts.event.projects.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.projects.ProjectCreated;
import pl.feature.toggle.service.contracts.shared.EventProcessor;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = "project-env-events")
class KafkaEventConsumer {

    private final ProjectEnvironmentProjectionUseCase projectionUseCase;
    private final EventProcessor eventProcessor;

    @KafkaHandler
    void handle(ProjectCreated event) {
        eventProcessor.process(event, projectionUseCase::handle);
    }

    @KafkaHandler
    void handle(EnvironmentCreated event) {
        eventProcessor.process(event, projectionUseCase::handle);
    }

}
