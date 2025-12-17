package pl.feature.toggle.service.infrastructure.out.db;

import com.ftaas.domain.featuretoggle.FeatureToggleId;
import com.ftaas.domain.featuretoggle.FeatureToggleName;
import github.saqie.ftaas.jooq.tables.records.FeatureToggleRecord;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;

import java.util.Optional;

import static github.saqie.ftaas.jooq.Tables.*;

@AllArgsConstructor
class FeatureToggleJooqRepository implements FeatureToggleRepository {

    private final DSLContext dslContext;


    @Override
    public void save(FeatureToggle featureToggle) {
        dslContext.insertInto(FEATURE_TOGGLE)
                .set(Mapper.toRecord(featureToggle))
                .execute();
    }

    @Override
    public Optional<FeatureToggle> findById(FeatureToggleId featureToggleId) {
        return dslContext.selectFrom(FEATURE_TOGGLE)
                .where(FEATURE_TOGGLE.ID.eq(featureToggleId.uuid()))
                .fetchOptional()
                .map(Mapper::toDomain);

    }

    @Override
    public void update(FeatureToggle featureToggle) {
        var record = Mapper.toRecord(featureToggle);
        record.attach(dslContext.configuration());
        record.update();
    }

    @Override
    public void delete(FeatureToggle featureToggle) {
        dslContext.deleteFrom(FEATURE_TOGGLE)
                .where(FEATURE_TOGGLE.ID.eq(featureToggle.id().uuid()))
                .execute();
    }

    @Override
    public boolean exists(FeatureToggleId featureToggleId) {
        return dslContext.fetchExists(FEATURE_TOGGLE, FEATURE_TOGGLE.ID.eq(featureToggleId.uuid()));
    }

    @Override
    public boolean exists(FeatureToggleName name) {
        return dslContext.fetchExists(FEATURE_TOGGLE, FEATURE_TOGGLE.NAME.eq(name.value()));
    }

}
