package com.configly.toggle.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import com.configly.model.project.ProjectId;
import com.configly.toggle.write.application.port.out.ProjectRefQueryRepository;
import com.configly.toggle.write.domain.reference.ProjectRef;

import java.util.Optional;

import static github.saqie.ftaas.jooq.tables.ProjectRef.PROJECT_REF;

@AllArgsConstructor
class ProjectRefQueryJooqRepository implements ProjectRefQueryRepository {

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
}
