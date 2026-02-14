package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.ProjectRefProjectionRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;

import java.util.Optional;

import static github.saqie.ftaas.jooq.tables.ProjectRef.PROJECT_REF;


@AllArgsConstructor
class ProjectRefProjectionJooqRepository implements ProjectRefProjectionRepository {

    private final DSLContext dslContext;

    @Override
    public void insert(ProjectRef ref) {
        dslContext.insertInto(PROJECT_REF)
                .set(PROJECT_REF.ID, ref.projectId().uuid())
                .set(PROJECT_REF.STATUS, ref.status().name())
                .set(PROJECT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(PROJECT_REF.CONSISTENT, ref.consistent())
                .execute();
    }

    @Override
    public void update(ProjectRef ref) {
        var rows = dslContext.update(PROJECT_REF)
                .set(PROJECT_REF.STATUS, ref.status().name())
                .set(PROJECT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(PROJECT_REF.CONSISTENT, ref.consistent())
                .where(PROJECT_REF.ID.eq(ref.projectId().uuid()))
                .execute();

        if (rows == 0) {
            throw new ProjectNotFoundException(ref.projectId());
        }
    }

    @Override
    public void upsert(ProjectRef ref) {
        dslContext.insertInto(PROJECT_REF)
                .set(PROJECT_REF.ID, ref.projectId().uuid())
                .set(PROJECT_REF.STATUS, ref.status().name())
                .set(PROJECT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(PROJECT_REF.CONSISTENT, ref.consistent())
                .onConflict(PROJECT_REF.ID)
                .doUpdate()
                .set(PROJECT_REF.STATUS, ref.status().name())
                .set(PROJECT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(PROJECT_REF.CONSISTENT, ref.consistent())
                .execute();
    }

    @Override
    public boolean markInconsistentIfNotMarked(ProjectId projectId) {
        var rows = dslContext.update(PROJECT_REF)
                .set(PROJECT_REF.CONSISTENT, false)
                .where(PROJECT_REF.ID.eq(projectId.uuid()))
                .and(PROJECT_REF.CONSISTENT.eq(true))
                .execute();
        return rows == 1;
    }
}
