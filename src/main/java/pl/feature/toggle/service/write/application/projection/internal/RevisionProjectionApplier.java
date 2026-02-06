package pl.feature.toggle.service.write.application.projection.internal;

import pl.feature.toggle.service.model.Revision;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RevisionProjectionApplier<T> {

    public void apply(
            Revision incoming,
            Supplier<Optional<T>> findCurrent,
            Runnable insert,
            Function<T, Revision> currentRevision,
            Consumer<T> update,
            BooleanSupplier markInconsistentIfNotMarked,
            Runnable publishRebuild
    ) {
        var currentOpt = findCurrent.get();
        if (currentOpt.isEmpty()) {
            insert.run();
            return;
        }

        var current = currentOpt.get();
        var currentRev = currentRevision.apply(current);

        if (incoming.isOutdatedComparedTo(currentRev)) {
            return;
        }

        if (incoming.indicatesGapAfter(currentRev)) {
            if (markInconsistentIfNotMarked.getAsBoolean()) {
                publishRebuild.run();
            }
            return;
        }

        if (incoming.canBeAppliedAfter(currentRev)) {
            update.accept(current);
        }
    }
}
