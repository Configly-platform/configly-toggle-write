package pl.feature.toggle.service.write.application.projection.internal;

import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.Revision;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionProjectionApplierTest {
    private final RevisionProjectionApplier<RefStub> sut = new RevisionProjectionApplier<>();

    @Test
    void should_insert_when_missing_and_incoming_is_initial() {
        // given
        var fx = Fixture.missing();
        var incoming = Revision.initialRevision();

        // when
        fx.apply(incoming);

        // then
        fx.assertInserted();
        fx.assertNoUpdates();
        fx.assertNoInconsistentMark();
        fx.assertNoPublish();
    }

    @Test
    void should_update_when_incoming_is_next_revision() {
        // given
        var current = new RefStub(Revision.from(2));
        var fx = Fixture.existing(current);
        var incoming = current.lastRevision().next();

        // when
        fx.apply(incoming);

        // then
        fx.assertUpdated();
        fx.assertNoInsert();
        fx.assertNoInconsistentMark();
        fx.assertNoPublish();
    }

    @Test
    void should_do_nothing_when_event_is_duplicate() {
        // given
        var current = new RefStub(Revision.from(2));
        var fx = Fixture.existing(current);
        var incoming = Revision.from(2);

        // when
        fx.apply(incoming);

        // then
        fx.assertNoInsert();
        fx.assertNoUpdates();
        fx.assertNoInconsistentMark();
        fx.assertNoPublish();
    }

    @Test
    void should_do_nothing_when_event_is_outdated() {
        // given
        var current = new RefStub(Revision.from(5));
        var fx = Fixture.existing(current);
        var incoming = Revision.from(2);

        // when
        fx.apply(incoming);

        // then
        fx.assertNoInsert();
        fx.assertNoUpdates();
        fx.assertNoInconsistentMark();
        fx.assertNoPublish();
    }

    @Test
    void should_mark_inconsistent_and_publish_when_gap_detected_and_first_time_marked() {
        // given
        var current = new RefStub(Revision.from(2));
        var fx = Fixture.existing(current).markReturns(true);
        var incoming = Revision.from(5); // gap > next

        // when
        fx.apply(incoming);

        // then
        fx.assertNoInsert();
        fx.assertNoUpdates();
        fx.assertMarkedInconsistent();
        fx.assertPublished();
    }

    @Test
    void should_not_publish_when_gap_detected_but_already_marked_inconsistent() {
        // given
        var current = new RefStub(Revision.from(2));
        var fx = Fixture.existing(current).markReturns(false);
        var incoming = Revision.from(5); // gap > next

        // when
        fx.apply(incoming);

        // then
        fx.assertNoInsert();
        fx.assertNoUpdates();
        fx.assertMarkedInconsistent();
        fx.assertNoPublish();
    }

    // ===== fixture =====

    private static final class RefStub {
        private final Revision lastRevision;

        private RefStub(Revision lastRevision) {
            this.lastRevision = lastRevision;
        }

        Revision lastRevision() {
            return lastRevision;
        }
    }

    private final class Fixture {
        private RefStub current;
        private int insertCalls;
        private int updateCalls;
        private int markCalls;
        private int publishCalls;
        private boolean markReturn = true;

        static Fixture missing() {
            var fx = new RevisionProjectionApplierTest().new Fixture();
            fx.current = null;
            return fx;
        }

        static Fixture existing(RefStub current) {
            var fx = new RevisionProjectionApplierTest().new Fixture();
            fx.current = current;
            return fx;
        }

        Fixture markReturns(boolean value) {
            this.markReturn = value;
            return this;
        }

        void apply(Revision incoming) {
            sut.apply(
                    incoming,
                    () -> Optional.ofNullable(current),
                    () -> insertCalls++,
                    RefStub::lastRevision,
                    __ -> updateCalls++,
                    () -> {
                        markCalls++;
                        return markReturn;
                    },
                    () -> publishCalls++
            );
        }

        void assertInserted() {
            assertThat(insertCalls).isEqualTo(1);
        }

        void assertNoInsert() {
            assertThat(insertCalls).isEqualTo(0);
        }

        void assertUpdated() {
            assertThat(updateCalls).isEqualTo(1);
        }

        void assertNoUpdates() {
            assertThat(updateCalls).isEqualTo(0);
        }

        void assertMarkedInconsistent() {
            assertThat(markCalls).isEqualTo(1);
        }

        void assertNoInconsistentMark() {
            assertThat(markCalls).isEqualTo(0);
        }

        void assertPublished() {
            assertThat(publishCalls).isEqualTo(1);
        }

        void assertNoPublish() {
            assertThat(publishCalls).isEqualTo(0);
        }
    }
}