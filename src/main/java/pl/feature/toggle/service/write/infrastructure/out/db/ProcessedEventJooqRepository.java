package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.event.processing.api.ProcessedEventRepository;

import static github.saqie.ftaas.jooq.tables.ProcessedEvents.PROCESSED_EVENTS;


@AllArgsConstructor
class ProcessedEventJooqRepository implements ProcessedEventRepository {

    private final DSLContext dslContext;


    @Override
    public boolean tryMarkProcessed(EventId eventId) {
        int inserted = dslContext
                .insertInto(PROCESSED_EVENTS)
                .set(PROCESSED_EVENTS.ID, eventId.id())
                .onConflictDoNothing()
                .execute();

        return inserted == 1;
    }
}
