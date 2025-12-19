package pl.feature.toggle.service.write.infrastructure;

import pl.feature.toggle.service.write.application.port.out.ProcessedEventRepository;
import pl.feature.toggle.service.contracts.shared.EventId;

import java.util.HashSet;
import java.util.Set;

public class FakeProcessedEventRepository implements ProcessedEventRepository {

    private final Set<EventId> events = new HashSet<>();

    @Override
    public boolean alreadyProcessed(EventId eventId) {
        return events.contains(eventId);
    }

    @Override
    public void markProcessed(EventId eventId) {
        if (alreadyProcessed(eventId)) {
            throw new IllegalStateException("Event already processed");
        }
        events.add(eventId);
    }
}
