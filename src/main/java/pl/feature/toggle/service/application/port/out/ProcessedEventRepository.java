package pl.feature.toggle.service.application.port.out;

import com.ftaas.contracts.shared.EventId;

public interface ProcessedEventRepository {

    boolean alreadyProcessed(EventId eventId);

    void markProcessed(EventId eventId);

}
