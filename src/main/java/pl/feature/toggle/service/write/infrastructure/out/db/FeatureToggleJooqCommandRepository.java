package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleUpdateResult;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleUpdateFailedException;

import static github.saqie.ftaas.jooq.tables.FeatureToggle.FEATURE_TOGGLE;
import static pl.feature.toggle.service.write.infrastructure.out.db.DatabaseUniqueConstraintExceptionHandler.translateUniqueConstraintException;


@AllArgsConstructor
class FeatureToggleJooqCommandRepository implements FeatureToggleCommandRepository {

    private final DSLContext dslContext;


    @Override
    public void save(FeatureToggle featureToggle) {
        translateUniqueConstraintException(
                () -> dslContext.insertInto(FEATURE_TOGGLE)
                        .set(Mapper.toRecord(featureToggle))
                        .execute(),
                featureToggle
        );

    }

    @Override
    public void update(FeatureToggleUpdateResult updateResult) {
        var featureToggle = updateResult.featureToggle();
        var rows = translateUniqueConstraintException(
                () -> dslContext.update(FEATURE_TOGGLE)
                        .set(FEATURE_TOGGLE.ID, featureToggle.id().uuid())
                        .set(FEATURE_TOGGLE.NAME, featureToggle.name().value())
                        .set(FEATURE_TOGGLE.DESCRIPTION, featureToggle.description().value())
                        .set(FEATURE_TOGGLE.CURRENT_VALUE, featureToggle.value().asText())
                        .set(FEATURE_TOGGLE.TYPE, featureToggle.value().typeName())
                        .set(FEATURE_TOGGLE.REVISION, featureToggle.revision().value())
                        .set(FEATURE_TOGGLE.STATUS, featureToggle.status().name())
                        .set(FEATURE_TOGGLE.CREATED_AT, featureToggle.createdAt().toLocalDateTime())
                        .set(FEATURE_TOGGLE.UPDATED_AT, featureToggle.updatedAt().toLocalDateTime())
                        .where(FEATURE_TOGGLE.ID.eq(featureToggle.id().uuid()))
                        .and(FEATURE_TOGGLE.REVISION.eq(updateResult.expectedRevision().value()))
                        .execute(),
                featureToggle
        );
        if (rows == 0) {
            throw new FeatureToggleUpdateFailedException(updateResult);
        }
    }

}
