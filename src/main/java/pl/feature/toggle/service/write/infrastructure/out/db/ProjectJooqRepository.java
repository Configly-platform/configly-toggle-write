package pl.feature.toggle.service.write.infrastructure.out.db;

import pl.feature.toggle.service.write.application.port.out.ProjectRepository;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.project.ProjectId;

import java.util.Optional;

import static github.saqie.ftaas.jooq.Tables.PROJECT_SNAPSHOT;

@AllArgsConstructor
class ProjectJooqRepository implements ProjectRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<ProjectSnapshot> findById(ProjectId projectId) {
        return dslContext.selectFrom(PROJECT_SNAPSHOT)
                .where(PROJECT_SNAPSHOT.ID.eq(projectId.uuid()))
                .fetchOptional()
                .map(Mapper::toDomain);
    }

    @Override
    public void save(ProjectSnapshot projectSnapshot) {
        dslContext.insertInto(PROJECT_SNAPSHOT)
                .set(Mapper.toRecord(projectSnapshot))
                .execute();
    }


}
