package com.configly.toggle.write.infrastructure.in.internal;

import lombok.AllArgsConstructor;
import org.springframework.transaction.event.TransactionalEventListener;
import com.configly.toggle.write.application.port.in.ArchiveFeatureTogglesByEnvironmentUseCase;
import com.configly.toggle.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;
import com.configly.toggle.write.application.projection.environment.event.EnvironmentArchivedCascadeRequest;

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
