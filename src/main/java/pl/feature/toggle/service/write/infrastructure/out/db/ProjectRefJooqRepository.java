package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;
import pl.feature.toggle.service.write.domain.reference.exception.ProjectNotFoundException;

import static github.saqie.ftaas.jooq.tables.ProjectRef.PROJECT_REF;


@AllArgsConstructor
class ProjectRefJooqRepository implements ProjectRefRepository {

    private final DSLContext dslContext;

    @Override
    public ProjectRef getOrThrow(ProjectId projectId) {
        return dslContext.selectFrom(PROJECT_REF)
                .where(PROJECT_REF.ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toDomain)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
    }

    @Override
    public void upsert(ProjectRef projectRef) {
        dslContext.insertInto(PROJECT_REF)
                .set(Mapper.toRecord(projectRef))
                .onConflict(PROJECT_REF.ID)
                .doUpdate()
                .set(PROJECT_REF.STATUS, projectRef.status().name())
                .execute();
    }


}
