package com.configly.toggle.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import com.configly.model.environment.EnvironmentId;
import com.configly.toggle.write.application.port.out.EnvironmentRefProjectionRepository;
import com.configly.toggle.write.domain.reference.EnvironmentRef;
import com.configly.toggle.write.domain.reference.exception.ProjectNotFoundException;

import static com.configly.jooq.tables.EnvironmentRef.ENVIRONMENT_REF;


@AllArgsConstructor
class EnvironmentRefProjectionJooqRepository implements EnvironmentRefProjectionRepository {

    private final DSLContext dslContext;


    @Override
    public void insert(EnvironmentRef ref) {
        dslContext.insertInto(ENVIRONMENT_REF)
                .set(ENVIRONMENT_REF.ID, ref.environmentId().uuid())
                .set(ENVIRONMENT_REF.STATUS, ref.status().name())
                .set(ENVIRONMENT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(ENVIRONMENT_REF.CONSISTENT, ref.consistent())
                .set(ENVIRONMENT_REF.PROJECT_ID, ref.projectId().uuid())
                .execute();
    }

    @Override
    public void update(EnvironmentRef ref) {
        var rows = dslContext.update(ENVIRONMENT_REF)
                .set(ENVIRONMENT_REF.STATUS, ref.status().name())
                .set(ENVIRONMENT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(ENVIRONMENT_REF.CONSISTENT, ref.consistent())
                .where(ENVIRONMENT_REF.ID.eq(ref.environmentId().uuid()))
                .execute();

        if (rows == 0) {
            throw new ProjectNotFoundException(ref.projectId());
        }
    }

    @Override
    public void upsert(EnvironmentRef ref) {
        dslContext.insertInto(ENVIRONMENT_REF)
                .set(ENVIRONMENT_REF.ID, ref.environmentId().uuid())
                .set(ENVIRONMENT_REF.STATUS, ref.status().name())
                .set(ENVIRONMENT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(ENVIRONMENT_REF.PROJECT_ID, ref.projectId().uuid())
                .set(ENVIRONMENT_REF.CONSISTENT, ref.consistent())
                .onConflict(ENVIRONMENT_REF.ID)
                .doUpdate()
                .set(ENVIRONMENT_REF.STATUS, ref.status().name())
                .set(ENVIRONMENT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(ENVIRONMENT_REF.CONSISTENT, ref.consistent())
                .execute();
    }

    @Override
    public boolean markInconsistentIfNotMarked(EnvironmentId environmentId) {
        var rows = dslContext.update(ENVIRONMENT_REF)
                .set(ENVIRONMENT_REF.CONSISTENT, false)
                .where(ENVIRONMENT_REF.ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_REF.CONSISTENT.eq(true))
                .execute();
        return rows == 1;
    }
}
