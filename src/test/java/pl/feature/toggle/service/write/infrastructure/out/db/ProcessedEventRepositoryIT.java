package pl.feature.toggle.service.write.infrastructure.out.db;

import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.out.ProcessedEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.contracts.shared.EventId;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessedEventRepositoryIT extends AbstractITTest {

    @Autowired
    ProcessedEventRepository sut;

    @Test
    @DisplayName("Should mark as processed given event")
    void test01() {
        // given
        var eventId = EventId.create();

        // when
        sut.markProcessed(eventId);

        // then
        var result = sut.alreadyProcessed(eventId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true if event was already processed")
    void test02() {
        // given
        var eventId = EventId.create();
        sut.markProcessed(eventId);

        // when
        var result = sut.alreadyProcessed(eventId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false if event was not processed yet")
    void test03() {
        // given
        var eventId = EventId.create();

        // when
        var result = sut.alreadyProcessed(eventId);

        // then
        assertThat(result).isFalse();
    }

}