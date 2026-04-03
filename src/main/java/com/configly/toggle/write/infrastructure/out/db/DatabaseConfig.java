package com.configly.toggle.write.infrastructure.out.db;


import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.jooq.autoconfigure.SpringTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import com.configly.event.processing.api.ProcessedEventRepository;
import com.configly.toggle.write.application.port.out.*;

import javax.sql.DataSource;

@Configuration("databaseConfig")
class DatabaseConfig {

    @Bean
    DefaultConfiguration jooqConfiguration(DataSource ds, PlatformTransactionManager txManager) {
        var settings = new Settings()
                .withRenderQuotedNames(RenderQuotedNames.NEVER)
                .withRenderNameCase(RenderNameCase.LOWER)
                .withRenderSchema(false);

        var cfg = new DefaultConfiguration();
        cfg.setSQLDialect(SQLDialect.POSTGRES);
        cfg.setSettings(settings);

        var txAwareDs = new TransactionAwareDataSourceProxy(ds);
        cfg.setConnectionProvider(new DataSourceConnectionProvider(txAwareDs));

        cfg.setTransactionProvider(new SpringTransactionProvider(txManager));

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
