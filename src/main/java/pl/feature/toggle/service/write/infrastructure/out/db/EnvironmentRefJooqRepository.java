package pl.feature.toggle.service.write.infrastructure.out.db;

import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.exception.EnvironmentNotFoundException;

import static github.saqie.ftaas.jooq.tables.EnvironmentRef.ENVIRONMENT_REF;


@AllArgsConstructor
class EnvironmentRefJooqRepository implements EnvironmentRefRepository {

    private final DSLContext dslContext;

    @Override
    public EnvironmentRef getOrThrow(ProjectId projectId, EnvironmentId environmentId) {
        return dslContext.selectFrom(ENVIRONMENT_REF)
                .where(ENVIRONMENT_REF.ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_REF.PROJECT_ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toDomain)
                .orElseThrow(() -> new EnvironmentNotFoundException(projectId, environmentId));
    }

    @Override
    public void upsert(EnvironmentRef environmentRef) {
        dslContext.insertInto(ENVIRONMENT_REF)
                .set(Mapper.toRecord(environmentRef))
                .onConflict(ENVIRONMENT_REF.ID)
                .doUpdate()
                .set(ENVIRONMENT_REF.PROJECT_ID, environmentRef.projectId().uuid())
                .set(ENVIRONMENT_REF.STATUS, environmentRef.status().name())
                .execute();
    }
}
