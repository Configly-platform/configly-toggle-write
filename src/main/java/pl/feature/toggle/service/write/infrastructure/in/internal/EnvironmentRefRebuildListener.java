package pl.feature.toggle.service.write.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.feature.toggle.service.write.application.port.in.EnvironmentRefConsistency;
import pl.feature.toggle.service.write.application.projection.environment.event.RebuildEnvironmentRefRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class EnvironmentRefRebuildListener {

    private final EnvironmentRefConsistency environmentRefConsistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(RebuildEnvironmentRefRequested event) {
        environmentRefConsistency.rebuild(event.projectId(), event.environmentId());
    }

}
