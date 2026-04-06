package com.configly.toggle.write;

import com.configly.jooq.tables.records.ProcessedEventsRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static com.configly.jooq.tables.EnvironmentRef.ENVIRONMENT_REF;
import static com.configly.jooq.tables.FeatureToggle.FEATURE_TOGGLE;
import static com.configly.jooq.tables.ProcessedEvents.PROCESSED_EVENTS;
import static com.configly.jooq.tables.ProjectRef.PROJECT_REF;
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
    protected DSLContext dslContext;

    @AfterEach
    void tearDown() {
        clearOutbox();
        clearFeatureToggles();
        clearEnvironments();
        clearProjects();
        clearProcessedEvents();
    }

    private void clearFeatureToggles() {
        dslContext.deleteFrom(FEATURE_TOGGLE).execute();
    }

    private void clearEnvironments() {
        dslContext.deleteFrom(ENVIRONMENT_REF).execute();
    }

    private void clearProjects() {
        dslContext.deleteFrom(PROJECT_REF).execute();
    }

    protected void clearOutbox() {
        dslContext.deleteFrom(OUTBOX_EVENTS).execute();
    }

    protected void clearProcessedEvents() {
        dslContext.deleteFrom(PROCESSED_EVENTS).execute();
    }

    protected List<ProcessedEventsRecord> getProcessedEvents() {
        return dslContext.selectFrom(PROCESSED_EVENTS).fetch();
    }


}
