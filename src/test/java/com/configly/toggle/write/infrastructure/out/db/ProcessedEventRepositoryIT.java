package com.configly.toggle.write.infrastructure.out.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.configly.contracts.shared.EventId;
import com.configly.event.processing.api.ProcessedEventRepository;
import com.configly.toggle.write.AbstractITTest;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessedEventRepositoryIT extends AbstractITTest {

    @Autowired
    ProcessedEventRepository sut;

    @Test
    void should_mark_event_as_processed_only_once() {
        // given
        var eventId = EventId.create();

        // when
        var first = sut.tryMarkProcessed(eventId);
        var second = sut.tryMarkProcessed(eventId);

        // then
        assertThat(first).isTrue();
        assertThat(second).isFalse();
    }

    @Test
    void should_mark_each_distinct_event_as_processed() {
        // given
        var eventId1 = EventId.create();
        var eventId2 = EventId.create();

        // when
        var first = sut.tryMarkProcessed(eventId1);
        var second = sut.tryMarkProcessed(eventId2);

        // then
        assertThat(first).isTrue();
        assertThat(second).isTrue();
    }

}