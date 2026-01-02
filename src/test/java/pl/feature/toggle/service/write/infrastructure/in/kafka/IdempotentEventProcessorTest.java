package pl.feature.toggle.service.write.infrastructure.in.kafka;

import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.contracts.shared.EventProcessor;
import pl.feature.toggle.service.write.infrastructure.FakeProcessedEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;
import static pl.feature.toggle.service.contracts.event.projects.ProjectCreated.projectCreatedEventBuilder;

class IdempotentEventProcessorTest extends AbstractUnitTest {

    private EventProcessor sut;

    @BeforeEach
    void setUp() {
        sut = new IdempotentEventProcessor(new FakeProcessedEventRepository());
    }

    @Test
    @DisplayName("Should process the same event only once")
    void test01() {
        // given
        var event = projectCreatedEventBuilder()
                .eventId(EventId.create())
                .build();

        var handler = mock(Consumer.class);

        // when
        sut.process(event, handler, () -> {});
        sut.process(event, handler, () -> {});

        // then
        verify(handler, times(1)).accept(event);
    }

}