package pl.feature.toggle.service.infrastructure.out.db;


import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.application.port.out.ProcessedEventRepository;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    FeatureToggleRepository featureToggleRepository(DSLContext dslContext) {
        return new FeatureToggleJooqRepository(dslContext);
    }

    @Bean
    ProjectRepository projectRepository(DSLContext dslContext) {
        return new ProjectJooqRepository(dslContext);
    }

    @Bean
    EnvironmentRepository environmentRepository(DSLContext dslContext) {
        return new EnvironmentJooqRepository(dslContext);
    }

    @Bean
    ProcessedEventRepository processedEventRepository(DSLContext dslContext) {
        return new ProcessedEventJooqRepository(dslContext);
    }

}
