package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;

import java.util.Optional;

import static github.saqie.ftaas.jooq.tables.ProjectRef.PROJECT_REF;


@AllArgsConstructor
class ProjectRefJooqRepository implements ProjectRefRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<ProjectRef> find(ProjectId projectId) {
        return dslContext.selectFrom(PROJECT_REF)
                .where(PROJECT_REF.ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toReference);
    }

    @Override
    public Optional<ProjectRef> findConsistent(ProjectId projectId) {
        return dslContext.selectFrom(PROJECT_REF)
                .where(PROJECT_REF.ID.eq(projectId.uuid()))
                .and(PROJECT_REF.CONSISTENT.eq(true))
                .fetchOptional()
                .map(Mapper::toReference);
    }

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
                .set(PROJECT_REF.CONSISTENT, true)
                .onConflict(PROJECT_REF.ID)
                .doUpdate()
                .set(PROJECT_REF.STATUS, ref.status().name())
                .set(PROJECT_REF.LAST_REVISION, ref.lastRevision().value())
                .set(PROJECT_REF.CONSISTENT, true)
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
