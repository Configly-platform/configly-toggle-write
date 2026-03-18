package pl.feature.toggle.service.write.infrastructure.out.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.exception.IntegrityConstraintViolationException;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;

import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class DatabaseUniqueConstraintExceptionHandler {

    static Integer translateUniqueConstraintException(Supplier<Integer> action, FeatureToggle featureToggle) {
        try {
            return action.get();
        } catch (IntegrityConstraintViolationException e) {
            throw new FeatureToggleAlreadyExistsException(featureToggle.name());
        }
    }
}
