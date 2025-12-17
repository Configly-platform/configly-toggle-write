package pl.feature.toggle.service.infrastructure.in.kafka;

import com.ftaas.contracts.event.projects.EnvironmentCreated;
import com.ftaas.contracts.event.projects.ProjectCreated;
import com.ftaas.contracts.shared.EventProcessor;
import pl.feature.toggle.service.application.port.in.ProjectEnvironmentProjectionUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

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
