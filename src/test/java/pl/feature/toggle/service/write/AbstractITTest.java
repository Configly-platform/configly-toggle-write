package pl.feature.toggle.service.write;

import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.feature.toggle.service.model.project.ProjectId;

import static github.saqie.ftaas.jooq.tables.EnvironmentSnapshot.ENVIRONMENT_SNAPSHOT;
import static github.saqie.ftaas.jooq.tables.FeatureToggle.FEATURE_TOGGLE;
import static github.saqie.ftaas.jooq.tables.ProcessedEvents.PROCESSED_EVENTS;
import static github.saqie.ftaas.jooq.tables.ProjectSnapshot.PROJECT_SNAPSHOT;
import static pl.feature.ftaas.outbox.jooq.tables.OutboxEvents.OUTBOX_EVENTS;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractITTest {

    @DynamicPropertySource
    static void pgProps(DynamicPropertyRegistry r) {
        var pg = PostgresContainer.getInstance();
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ProjectRefRepository projectRefRepository;

    @Autowired
    private EnvironmentRefRepository environmentRefRepository;

    @AfterEach
    void tearDown() {
        clearOutbox();
        clearFeatureToggles();
        clearEnvironments();
        clearProjects();
        clearProcessedEvents();
    }

    private void clearFeatureToggles(){
        dslContext.deleteFrom(FEATURE_TOGGLE).execute();
    }

    private void clearEnvironments() {
        dslContext.deleteFrom(ENVIRONMENT_SNAPSHOT).execute();
    }

    private void clearProjects() {
        dslContext.deleteFrom(PROJECT_SNAPSHOT).execute();
    }

    protected void clearOutbox() {
        dslContext.deleteFrom(OUTBOX_EVENTS).execute();
    }

    protected void clearProcessedEvents() {
        dslContext.deleteFrom(PROCESSED_EVENTS).execute();
    }

    protected ProjectSnapshot createProject() {
        var projectSnapshot = ProjectSnapshot.create();
        projectRefRepository.upsert(projectSnapshot);
        return projectSnapshot;
    }

    protected EnvironmentSnapshot createEnvironment(ProjectId projectId) {
        var environmentSnapshot = EnvironmentSnapshot.create(projectId);
        environmentRefRepository.upsert(environmentSnapshot);
        return environmentSnapshot;
    }


}
