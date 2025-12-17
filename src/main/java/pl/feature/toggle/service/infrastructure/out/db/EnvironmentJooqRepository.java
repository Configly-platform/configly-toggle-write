package pl.feature.toggle.service.infrastructure.out.db;

import com.ftaas.domain.environment.EnvironmentId;
import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;

import java.util.Optional;

import static github.saqie.ftaas.jooq.Tables.ENVIRONMENT_SNAPSHOT;

@AllArgsConstructor
class EnvironmentJooqRepository implements EnvironmentRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<EnvironmentSnapshot> findById(EnvironmentId environmentId) {
        return dslContext.selectFrom(ENVIRONMENT_SNAPSHOT)
                .where(ENVIRONMENT_SNAPSHOT.ID.eq(environmentId.uuid()))
                .fetchOptional()
                .map(Mapper::toDomain);
    }

    @Override
    public void save(final EnvironmentSnapshot environmentSnapshot) {
        dslContext.insertInto(ENVIRONMENT_SNAPSHOT)
                .set(Mapper.toRecord(environmentSnapshot))
                .execute();
    }
}
