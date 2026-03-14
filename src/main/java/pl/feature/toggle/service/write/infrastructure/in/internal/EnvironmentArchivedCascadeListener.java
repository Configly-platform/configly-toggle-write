package pl.feature.toggle.service.write.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.feature.toggle.service.write.application.port.in.ArchiveFeatureTogglesByEnvironmentUseCase;
import pl.feature.toggle.service.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;
import pl.feature.toggle.service.write.application.projection.environment.event.EnvironmentArchivedCascadeRequest;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@AllArgsConstructor
class EnvironmentArchivedCascadeListener {

    private final ArchiveFeatureTogglesByEnvironmentUseCase useCase;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    void on(EnvironmentArchivedCascadeRequest event) {
        useCase.handle(new ArchiveFeatureTogglesByEnvironmentCommand(
                event.environmentId(),
                event.actor(),
                event.correlationId()
        ));
    }

}
