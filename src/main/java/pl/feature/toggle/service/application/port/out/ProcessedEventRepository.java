package pl.feature.toggle.service.application.port.out;


import pl.feature.toggle.service.contracts.shared.EventId;

public interface ProcessedEventRepository {

    boolean alreadyProcessed(EventId eventId);

    void markProcessed(EventId eventId);

}
