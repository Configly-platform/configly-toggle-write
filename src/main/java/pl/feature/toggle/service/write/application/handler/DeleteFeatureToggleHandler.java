package pl.feature.toggle.service.write.application.handler;

import pl.feature.toggle.service.write.application.port.in.DeleteFeatureToggleUseCase;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

import static pl.feature.toggle.service.write.application.handler.FeatureToggleHandlerEventMapper.createFeatureToggleDeletedEvent;
import static pl.feature.toggle.service.contracts.topic.KafkaTopic.FEATURE_TOGGLE;

@AllArgsConstructor
class DeleteFeatureToggleHandler implements DeleteFeatureToggleUseCase {

    private final FeatureToggleRepository featureToggleRepository;
    private final OutboxWriter outboxWriter;

    @Override
    @Transactional
    public void execute(FeatureToggleId featureToggleId) {
        var featureToggle = loadFeatureToggle(featureToggleId);

        featureToggleRepository.delete(featureToggle);

        var event = createFeatureToggleDeletedEvent(featureToggleId);
        outboxWriter.write(event, FEATURE_TOGGLE.topic());
    }

    private FeatureToggle loadFeatureToggle(FeatureToggleId featureToggleId) {
        return featureToggleRepository.findById(featureToggleId)
                .orElseThrow(() -> new FeatureToggleNotFoundException(featureToggleId));
    }
}
