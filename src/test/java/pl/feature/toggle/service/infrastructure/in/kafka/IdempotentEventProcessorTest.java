package pl.feature.toggle.service.infrastructure.in.kafka;

import com.ftaas.contracts.shared.EventId;
import com.ftaas.contracts.shared.EventProcessor;
import pl.feature.toggle.service.AbstractUnitTest;
import pl.feature.toggle.service.infrastructure.FakeProcessedEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static com.ftaas.contracts.event.projects.ProjectCreated.projectCreatedEventBuilder;
import static org.mockito.Mockito.*;

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
        sut.process(event, handler);
        sut.process(event, handler);

        // then
        verify(handler, times(1)).accept(event);
    }

}