package pl.feature.toggle.service.write.infrastructure.out.db;


import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.feature.toggle.service.event.processing.api.ProcessedEventRepository;
import pl.feature.toggle.service.write.application.port.out.EnvironmentRefRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleCommandRepository;
import pl.feature.toggle.service.write.application.port.out.FeatureToggleQueryRepository;
import pl.feature.toggle.service.write.application.port.out.ProjectRefRepository;

import javax.sql.DataSource;

@Configuration("databaseConfig")
class Config {

    @Bean
    DefaultConfiguration jooqConfiguration(DataSource ds) {
        var settings = new Settings()
                .withRenderQuotedNames(RenderQuotedNames.NEVER)
                .withRenderNameCase(RenderNameCase.LOWER)
                .withRenderSchema(false);

        var cfg = new DefaultConfiguration();
        cfg.set(ds);
        cfg.set(SQLDialect.POSTGRES);
        cfg.set(settings);
        return cfg;
    }

    @Bean
    FeatureToggleQueryRepository featureToggleQueryRepository(DSLContext dslContext) {
        return new FeatureToggleJooqQueryRepository(dslContext);
    }

    @Bean
    FeatureToggleCommandRepository featureToggleCommandRepository(DSLContext dslContext) {
        return new FeatureToggleJooqCommandRepository(dslContext);
    }

    @Bean
    ProjectRefRepository projectRepository(DSLContext dslContext) {
        return new ProjectRefJooqRepository(dslContext);
    }

    @Bean
    EnvironmentRefRepository environmentRepository(DSLContext dslContext) {
        return new EnvironmentRefJooqRepository(dslContext);
    }

    @Bean
    ProcessedEventRepository processedEventRepository(DSLContext dslContext) {
        return new ProcessedEventJooqRepository(dslContext);
    }

}
