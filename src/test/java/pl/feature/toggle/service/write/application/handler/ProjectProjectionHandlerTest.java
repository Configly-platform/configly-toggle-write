package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.write.AbstractUnitTest;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.project.ProjectCreated.projectCreatedEventBuilder;

class ProjectProjectionHandlerTest extends AbstractUnitTest {

    private ProjectProjection sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.projectProjection(projectRepository, environmentRepository);
    }

    @Test
    @DisplayName("Should handle project created event")
    void test01() {
        // given
        var projectId = UUID.randomUUID();
        var projectCreated = projectCreatedEventBuilder()
                .projectId(projectId)
                .build();

        // when
        sut.handle(projectCreated);

        // then
        var result = projectRepository.findById(ProjectId.create(projectId));
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(ProjectSnapshot.from(projectCreated));
    }

    @Test
    @DisplayName("Should handle environment created event")
    void test02() {
        // given
        var environmentId = UUID.randomUUID();
        var environmentCreated = environmentCreatedEventBuilder()
                .projectId(UUID.randomUUID())
                .environmentId(environmentId)
                .build();

        // when
        sut.handle(environmentCreated);

        // then
        var result = environmentRepository.findById(EnvironmentId.create(environmentId));
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(EnvironmentSnapshot.from(environmentCreated));
    }
}
