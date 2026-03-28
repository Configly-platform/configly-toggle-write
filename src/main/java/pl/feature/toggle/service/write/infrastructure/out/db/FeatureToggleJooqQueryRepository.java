package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;

import java.util.List;

import static github.saqie.ftaas.jooq.tables.FeatureToggle.FEATURE_TOGGLE;


@AllArgsConstructor
class FeatureToggleJooqQueryRepository implements FeatureToggleQueryRepository {

    private final DSLContext dslContext;


    @Override
    public FeatureToggle getOrThrow(FeatureToggleId featureToggleId) {
        return dslContext.selectFrom(FEATURE_TOGGLE)
                .where(FEATURE_TOGGLE.ID.eq(featureToggleId.uuid()))
                .fetchOptional()
                .map(Mapper::toDomain)
                .orElseThrow(() -> new FeatureToggleNotFoundException(featureToggleId));

    }

    @Override
    public List<FeatureToggle> findByEnvironmentId(EnvironmentId environmentId) {
        return dslContext.selectFrom(FEATURE_TOGGLE)
                .where(FEATURE_TOGGLE.ENVIRONMENT_ID.eq(environmentId.uuid()))
                .fetch()
                .map(Mapper::toDomain);
    }

}
