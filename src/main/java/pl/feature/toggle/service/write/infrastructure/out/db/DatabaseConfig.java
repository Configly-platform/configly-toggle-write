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
import pl.feature.toggle.service.write.application.port.out.*;

import javax.sql.DataSource;

@Configuration("databaseConfig")
class DatabaseConfig {

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
    ProjectRefProjectionRepository projectRefProjectionRepository(DSLContext dslContext) {
        return new ProjectRefProjectionJooqRepository(dslContext);
    }

    @Bean
    EnvironmentRefProjectionRepository environmentRefProjectionRepository(DSLContext dslContext) {
        return new EnvironmentRefProjectionJooqRepository(dslContext);
    }

    @Bean
    ProcessedEventRepository processedEventRepository(DSLContext dslContext) {
        return new ProcessedEventJooqRepository(dslContext);
    }

    @Bean
    EnvironmentRefQueryRepository environmentRefQueryRepository(DSLContext dslContext) {
        return new EnvironmentRefQueryJooqRepository(dslContext);
    }

    @Bean
    ProjectRefQueryRepository projectRefQueryRepository(DSLContext dslContext) {
        return new ProjectRefQueryJooqRepository(dslContext);
    }


}
