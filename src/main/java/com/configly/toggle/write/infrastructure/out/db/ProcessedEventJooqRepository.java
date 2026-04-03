package com.configly.toggle.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import com.configly.contracts.shared.EventId;
import com.configly.event.processing.api.ProcessedEventRepository;

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
