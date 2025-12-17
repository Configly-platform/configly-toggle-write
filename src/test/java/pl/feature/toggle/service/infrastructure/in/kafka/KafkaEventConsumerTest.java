package pl.feature.toggle.service.infrastructure.in.kafka;

import com.ftaas.contracts.shared.EventProcessor;
import pl.feature.toggle.service.AbstractUnitTest;
import pl.feature.toggle.service.application.port.in.ProjectEnvironmentProjectionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.ftaas.contracts.event.projects.EnvironmentCreated.environmentCreatedEventBuilder;
import static com.ftaas.contracts.event.projects.ProjectCreated.projectCreatedEventBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaEventConsumerTest extends AbstractUnitTest {

    private KafkaEventConsumer sut;
    private EventProcessor eventProcessor;

    @BeforeEach
    void setUp() {
        var projectEnvProjectionUseCase = mock(ProjectEnvironmentProjectionUseCase.class);
        eventProcessor = mock(IdempotentEventProcessor.class);
        sut = new KafkaEventConsumer(projectEnvProjectionUseCase, eventProcessor);
    }

    @Test
    @DisplayName("Should handle ProjectCreated event")
    void test01() {
        // given
        var projectId = UUID.randomUUID();
        var projectCreated = projectCreatedEventBuilder()
                .projectId(projectId)
                .build();

        // when
        sut.handle(projectCreated);

        // then
        verify(eventProcessor).process(eq(projectCreated), any());
    }

    @Test
    @DisplayName("Should handle EnvironmentCreated event")
    void test02() {
        // given
        var environmentId = UUID.randomUUID();
        var environmentCreated = environmentCreatedEventBuilder()
                .projectId(UUID.randomUUID())
                .environmentId(environmentId)
                .build();

        // when
        sut.handle(environmentCreated);

        // then
        verify(eventProcessor).process(eq(environmentCreated), any());
    }

}