package pl.feature.toggle.service.write.infrastructure.in.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.event.processing.api.EventProcessor;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.FakeAcknowledgment;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;

class KafkaEventConsumerIT extends AbstractITTest {

    @Autowired
    private ProjectProjection projectProjection;
    @Autowired
    private EnvironmentProjection environmentProjection;
    @Autowired
    private EventProcessor eventProcessor;

    private FakeAcknowledgment fakeAcknowledgment;
    private KafkaEventConsumer sut;

    @BeforeEach
    void setUp() {
        fakeAcknowledgment = new FakeAcknowledgment();
        sut = new KafkaEventConsumer(projectProjection, environmentProjection, eventProcessor);
    }

    @Test
    void should_not_mark_event_as_processed_when_projection_handling_fails() {
        // given
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();

        var event = environmentCreatedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .environmentName("test")
                .status(EnvironmentStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        var exception = catchException(() -> sut.handle(event, fakeAcknowledgment));

        // then
        assertThat(exception).isNotNull();
        var processedEvents = getProcessedEvents();
        assertThat(processedEvents).isEmpty();
    }
}