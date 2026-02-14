package pl.feature.toggle.service.write.application.projection.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.model.project.ProjectStatus;
import pl.feature.toggle.service.write.AbstractITTest;
import pl.feature.toggle.service.write.application.port.in.ProjectProjection;
import pl.feature.toggle.service.write.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefQueryRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;

import java.time.Duration;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static pl.feature.toggle.service.contracts.event.project.ProjectCreated.projectCreatedEventBuilder;
import static pl.feature.toggle.service.contracts.event.project.ProjectStatusChanged.projectStatusChangedEventBuilder;

@Import(ProjectProjectionEventualConsistencyIT.SyncAsyncConfig.class)
class ProjectProjectionEventualConsistencyIT extends AbstractITTest {

    @Autowired
    private ProjectProjection sut;

    @Autowired
    private ProjectRefProjectionRepository projectRefProjectionRepository;

    @Autowired
    private ProjectRefQueryRepository queryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

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
    void should_rebuild_project_ref_after_commit_when_gap_detected() {
        // given
        var projectId = ProjectId.create();

        var existing = ProjectRef.from(projectId, ProjectStatus.ACTIVE, Revision.from(2));
        projectRefProjectionRepository.insert(existing);

        var rebuilt = ProjectRef.from(projectId, ProjectStatus.ARCHIVED, Revision.from(5));
        given(configurationClient.fetchProject(projectId)).willReturn(rebuilt);

        var gapEvent = projectStatusChangedEventBuilder()
                .projectId(projectId.uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(5)
                .build();

        // when
        transactionTemplate.executeWithoutResult(x -> sut.handle(gapEvent));

        // then
        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    var actual = queryRepository.find(projectId).orElseThrow();
                    assertThat(actual.lastRevision()).isEqualTo(Revision.from(5));
                    assertThat(actual.status()).isEqualTo(ProjectStatus.ARCHIVED);
                    assertThat(actual.consistent()).isTrue();
                });
    }

    @Test
    void should_create_projection_when_status_changed_arrives_before_created() {
        // given
        var projectId = ProjectId.create();

        var statusChangedFirst = projectStatusChangedEventBuilder()
                .projectId(projectId.uuid())
                .status(ProjectStatus.ARCHIVED.name())
                .revision(Revision.from(2).value())
                .build();

        var createdLater = projectCreatedEventBuilder()
                .projectId(projectId.uuid())
                .status(ProjectStatus.ACTIVE.name())
                .revision(Revision.initialRevision().value())
                .build();

        // when
        sut.handle(statusChangedFirst);
        sut.handle(createdLater);

        // then
        var actual = queryRepository.find(projectId).orElseThrow();
        assertThat(actual.projectId()).isEqualTo(projectId);

        assertThat(actual.status()).isEqualTo(ProjectStatus.ARCHIVED);
        assertThat(actual.lastRevision()).isEqualTo(Revision.from(2));
        assertThat(actual.consistent()).isTrue();
    }
}
