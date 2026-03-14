package pl.feature.toggle.service.write.application.projection.environment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.environment.EnvironmentStatus;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.time.Duration;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentCreated.environmentCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.environment.EnvironmentStatusChanged.environmentStatusChangedEventBuilder;
import static pl.feature.toggle.service.contracts.fake.event.FakeEnvironmentStatusChangedBuilder.fakeEnvironmentStatusChangedBuilder;

@Import(EnvironmentProjectionEventualConsistencyIT.SyncAsyncConfig.class)
class EnvironmentProjectionEventualConsistencyIT extends AbstractITTest {

    @Autowired
    private EnvironmentProjection sut;

    @Autowired
    private EnvironmentRefProjectionRepository environmentRefProjectionRepository;

    @Autowired
    private EnvironmentRefQueryRepository  environmentRefQueryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ProjectRefProjectionRepository projectRefProjectionRepository;

    @MockitoBean
    private ConfigurationClient configurationClient;

    @TestConfiguration
    static class SyncAsyncConfig {

        @Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
        public Executor taskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Test
    void should_rebuild_environment_ref_after_commit_when_gap_detected() {
        // given
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectRef = ProjectRef.from(projectId, ProjectStatus.ACTIVE, Revision.initialRevision());
        projectRefProjectionRepository.insert(projectRef);

        var existing = EnvironmentRef.from(projectId, envId, EnvironmentStatus.ACTIVE, Revision.from(2));
        environmentRefProjectionRepository.insert(existing);

        var rebuilt = EnvironmentRef.from(projectId, envId, EnvironmentStatus.ARCHIVED, Revision.from(5));
        given(configurationClient.fetchEnvironment(projectId, envId)).willReturn(rebuilt);

        var gapEvent = environmentStatusChangedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .status(EnvironmentStatus.ARCHIVED.name())
                .revision(5)
                .build();

        // when
        transactionTemplate.executeWithoutResult(x -> sut.handle(gapEvent));

        // then
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var actual = environmentRefQueryRepository.find(projectId, envId).orElseThrow();
                    assertThat(actual.lastRevision()).isEqualTo(Revision.from(5));
                    assertThat(actual.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
                    assertThat(actual.consistent()).isTrue();
                });
    }

    @Test
    void should_create_projection_when_status_changed_arrives_before_created() {
        // given
        var projectId = ProjectId.create();
        var envId = EnvironmentId.create();
        var projectRef = ProjectRef.from(projectId, ProjectStatus.ACTIVE, Revision.initialRevision());
        projectRefProjectionRepository.insert(projectRef);

        var statusChangedFirst = fakeEnvironmentStatusChangedBuilder()
                .withProjectId(projectId.uuid())
                .withEnvironmentId(envId.uuid())
                .withStatus(EnvironmentStatus.ARCHIVED.name())
                .withRevision(Revision.from(2).value())
                .build();

        var createdLater = environmentCreatedEventBuilder()
                .projectId(projectId.uuid())
                .environmentId(envId.uuid())
                .environmentName("test")
                .status(EnvironmentStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(statusChangedFirst);
        sut.handle(createdLater);

        // then
        var actual = environmentRefQueryRepository.find(projectId, envId).orElseThrow();
        assertThat(actual.environmentId()).isEqualTo(envId);
        assertThat(actual.projectId()).isEqualTo(projectId);

        assertThat(actual.status()).isEqualTo(EnvironmentStatus.ARCHIVED);
        assertThat(actual.lastRevision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isTrue();
    }
}
