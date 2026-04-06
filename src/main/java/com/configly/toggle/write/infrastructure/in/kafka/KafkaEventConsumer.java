package com.configly.toggle.write.infrastructure.in.kafka;

import org.springframework.kafka.support.Acknowledgment;
import com.configly.contracts.event.environment.EnvironmentStatusChanged;
import com.configly.contracts.event.project.ProjectStatusChanged;
import com.configly.contracts.event.project.ProjectUpdated;
import com.configly.event.processing.api.EventProcessor;
import com.configly.toggle.write.application.port.in.EnvironmentProjection;
import com.configly.toggle.write.application.port.in.ProjectProjection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import com.configly.contracts.event.environment.EnvironmentCreated;
import com.configly.contracts.event.project.ProjectCreated;

@Slf4j
@AllArgsConstructor
@KafkaListener(topics = "${topics.configly-structure-events}")
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

    @KafkaHandler
    void ignore(ProjectUpdated event, Acknowledgment acknowledgment) {
        log.debug("Ignoring ProjectUpdated event, because project name changes are irrelevant for write-service");
        acknowledgment.acknowledge();
    }

}
