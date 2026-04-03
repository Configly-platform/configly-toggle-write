package com.configly.toggle.write.application.projection.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import com.configly.model.Revision;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.toggle.write.AbstractITTest;
import com.configly.toggle.write.application.port.in.ProjectProjection;
import com.configly.toggle.write.application.port.out.ConfigurationClient;
import com.configly.toggle.write.application.port.out.ProjectRefProjectionRepository;
import com.configly.toggle.write.application.port.out.ProjectRefQueryRepository;
import com.configly.toggle.write.domain.reference.ProjectRef;

import java.time.Duration;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;
import static com.configly.contracts.fake.event.FakeProjectCreatedBuilder.fakeProjectCreatedBuilder;
import static com.configly.contracts.fake.event.FakeProjectStatusChangedBuilder.fakeProjectStatusChangedBuilder;

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

        var gapEvent = fakeProjectStatusChangedBuilder()
                .withProjectId(projectId.uuid())
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(5)
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

        var statusChangedFirst = fakeProjectStatusChangedBuilder()
                .withProjectId(projectId.uuid())
                .withStatus(ProjectStatus.ARCHIVED.name())
                .withRevision(Revision.from(2).value())
                .build();

        var createdLater = fakeProjectCreatedBuilder()
                .withProjectId(projectId.uuid())
                .withStatus(ProjectStatus.ACTIVE.name())
                .withRevision(Revision.initialRevision().value())
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
