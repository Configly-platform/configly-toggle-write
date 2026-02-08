package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefQueryRepository;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;

import java.util.Optional;

import static github.saqie.ftaas.jooq.tables.EnvironmentRef.ENVIRONMENT_REF;

@AllArgsConstructor
class EnvironmentRefQueryJooqRepository implements EnvironmentRefQueryRepository {

    private final DSLContext dslContext;


    @Override
    public Optional<EnvironmentRef> find(ProjectId projectId, EnvironmentId environmentId) {
        return dslContext.selectFrom(ENVIRONMENT_REF)
                .where(ENVIRONMENT_REF.ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_REF.PROJECT_ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toReference);
    }

    @Override
    public Optional<EnvironmentRef> findConsistent(ProjectId projectId, EnvironmentId environmentId) {
        return dslContext.selectFrom(ENVIRONMENT_REF)
                .where(ENVIRONMENT_REF.ID.eq(environmentId.uuid()))
                .and(ENVIRONMENT_REF.CONSISTENT.eq(true))
                .and(ENVIRONMENT_REF.PROJECT_ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toReference);
    }
}
