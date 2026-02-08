package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefProjectionRepository;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;

import java.util.Optional;

import static github.saqie.ftaas.jooq.tables.EnvironmentRef.ENVIRONMENT_REF;


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
                .set(ENVIRONMENT_REF.CONSISTENT, true)
                .onConflict(ENVIRONMENT_REF.ID)
                .doUpdate()
                .set(ENVIRONMENT_REF.STATUS, ref.status().name())
                .set(ENVIRONMENT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(ENVIRONMENT_REF.CONSISTENT, true)
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
