package com.configly.toggle.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.application.port.out.EnvironmentRefQueryRepository;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

import java.util.Optional;

import static com.configly.jooq.tables.EnvironmentRef.ENVIRONMENT_REF;


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
