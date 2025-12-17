package pl.feature.toggle.service.infrastructure;

import github.saqie.ftaasoutbox.api.OutboxWriter;
import pl.feature.toggle.service.application.handler.FeatureToggleHandlerFacade;
import pl.feature.toggle.service.application.port.in.CreateFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.application.port.in.UpdateFeatureToggleUseCase;
import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
