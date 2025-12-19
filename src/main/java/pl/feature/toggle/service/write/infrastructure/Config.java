package pl.feature.toggle.service.write.infrastructure;

import pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.write.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.write.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

@Configuration("featureToggleWriteConfiguration")
class Config {

    @Bean
    CreateFeatureToggleUseCase createFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.createFeatureToggleUseCase(featureToggleRepository, projectRepository, environmentRepository, outboxWriter);
    }

    @Bean
    DeleteFeatureToggleUseCase deleteFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.deleteFeatureToggleUseCase(featureToggleRepository, outboxWriter);
    }

    @Bean
    UpdateFeatureToggleUseCase updateFeatureToggleUseCase(
            FeatureToggleRepository featureToggleRepository,
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OutboxWriter outboxWriter
    ) {
        return FeatureToggleHandlerFacade.updateFeatureToggleUseCase(featureToggleRepository, projectRepository, environmentRepository, outboxWriter);
    }

    @Bean
    ProjectEnvironmentProjectionUseCase projectEnvironmentProjectionUseCase(
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository
    ) {
        return FeatureToggleHandlerFacade.projectEnvironmentProjectionUseCase(projectRepository, environmentRepository);
    }

}
