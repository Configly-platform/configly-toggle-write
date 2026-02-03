package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;

import static github.saqie.ftaas.jooq.tables.FeatureToggle.FEATURE_TOGGLE;


@AllArgsConstructor
class FeatureToggleJooqCommandRepository implements FeatureToggleCommandRepository {

    private final DSLContext dslContext;


    @Override
    public void save(FeatureToggle featureToggle) {
        dslContext.insertInto(FEATURE_TOGGLE)
                .set(Mapper.toRecord(featureToggle))
                .execute();
    }


    @Override
    public void update(FeatureToggle featureToggle) {
        var record = Mapper.toRecord(featureToggle);
        record.attach(dslContext.configuration());
        record.update();
    }

}
