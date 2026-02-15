package pl.feature.toggle.service.write.infrastructure.in.kafka;

import org.springframework.kafka.support.Acknowledgment;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged;
import pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged;
import pl.feature.toggle.service.event.processing.api.EventProcessor;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated;
import pl.feature.toggle.service.contracts.event.project.ProjectCreated;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = "${topics.feature-toggle-configuration-events}")
class KafkaEventConsumer {

    private final ProjectProjection projectProjection;
    private final EnvironmentProjection environmentProjection;
    private final EventProcessor eventProcessor;

    @KafkaHandler
    void handle(ProjectCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectProjection::handle,
                acknowledgment::acknowledge
        );
    }

    @KafkaHandler
    void handle(EnvironmentCreated event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                environmentProjection::handle,
                acknowledgment::acknowledge
        );
    }

    @KafkaHandler
    void handle(EnvironmentStatusChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                environmentProjection::handle,
                acknowledgment::acknowledge
        );
    }

    @KafkaHandler
    void handle(ProjectStatusChanged event, Acknowledgment acknowledgment) {
        eventProcessor.process(
                event,
                projectProjection::handle,
                acknowledgment::acknowledge
        );
    }

}
