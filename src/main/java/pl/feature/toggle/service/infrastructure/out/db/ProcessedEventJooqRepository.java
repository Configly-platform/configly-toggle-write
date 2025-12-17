package pl.feature.toggle.service.infrastructure.out.db;

import com.ftaas.contracts.shared.EventId;
import pl.feature.toggle.service.application.port.out.ProcessedEventRepository;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;

import static github.saqie.ftaas.jooq.Tables.PROCESSED_EVENTS;

@AllArgsConstructor
class ProcessedEventJooqRepository implements ProcessedEventRepository {

    private final DSLContext dslContext;

    @Override
    public boolean alreadyProcessed(EventId eventId) {
        return dslContext.fetchExists(PROCESSED_EVENTS, PROCESSED_EVENTS.ID.eq(eventId.id()));
    }

    @Override
    public void markProcessed(EventId eventId) {
        dslContext.insertInto(PROCESSED_EVENTS)
                .set(PROCESSED_EVENTS.ID, eventId.id())
                .execute();
    }
}
