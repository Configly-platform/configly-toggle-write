package pl.feature.toggle.service.application.handler;

import pl.feature.toggle.service.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureToggleHandlerFacade {

    public static CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter
    ) {
        return new CreateFeatureToggleHandler(featureToggleRepository, projectRepository, environmentRepository, outboxWriter);
    }

    public static UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter
    ) {
        return new UpdateFeatureToggleHandler(featureToggleRepository, projectRepository, environmentRepository, outboxWriter);
    }

    public static DeleteFeatureToggleUseCase deleteFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            OutboxWriter outboxWriter
    ) {
        return new DeleteFeatureToggleHandler(featureToggleRepository, outboxWriter);
    }

    public static ProjectEnvironmentProjectionUseCase projectEnvironmentProjectionUseCase(
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository
    ) {
        return new ProjectEnvironmentProjectionHandler(projectRepository, environmentRepository);
    }

}
