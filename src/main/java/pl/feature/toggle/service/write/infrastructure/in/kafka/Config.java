package pl.feature.toggle.service.write.infrastructure.in.kafka;

import pl.feature.toggle.service.write.application.port.in.ProjectEnvironmentProjectionUseCase;
import pl.feature.toggle.service.write.application.port.out.ProcessedEventRepository;
import pl.feature.toggle.service.contracts.shared.EventProcessor;
import pl.feature.toggle.service.write.exception.NotRetryableException;
import pl.feature.toggle.service.write.exception.RetryableException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE;

@Configuration("kafkaConfig")
@ConditionalOnProperty(name = "outbox.kafka.enabled", havingValue = "true")
class Config {

    @Autowired
    private Environment environment;

    @Bean
    ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("outbox.kafka.bootstrap-servers"));
        cfg.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        cfg.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        cfg.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        cfg.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        return new DefaultKafkaConsumerFactory<>(cfg);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties()
                .setAckMode(MANUAL_IMMEDIATE);

        var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

        var backOff = new FixedBackOff(5000, 3);

        var errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(NotRetryableException.class);
        errorHandler.addRetryableExceptions(RetryableException.class);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    EventProcessor eventProcessor(ProcessedEventRepository repository) {
        return new IdempotentEventProcessor(repository);
    }

    @Bean
    KafkaEventConsumer kafkaEventConsumer(ProjectEnvironmentProjectionUseCase projectionUseCase, EventProcessor eventProcessor) {
        return new KafkaEventConsumer(projectionUseCase, eventProcessor);
    }
}
