package pl.feature.toggle.service.write.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.feature.toggle.service.event.processing.api.CorrelationScope;
import pl.feature.toggle.service.write.application.port.in.ProjectRefConsistency;
import pl.feature.toggle.service.write.application.projection.project.event.RebuildProjectRefRequested;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class ProjectRefRebuildListener {

    private final ProjectRefConsistency projectRefConsistency;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(RebuildProjectRefRequested event) {
        CorrelationScope.run(
                event.correlationId(),
                () -> projectRefConsistency.rebuild(event.projectId())
        );
    }

}
