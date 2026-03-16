package com.glowxq.triflow.base.configuration;

import com.glowxq.common.mysql.properties.FlywayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.flywaydb.core.internal.exception.FlywaySqlException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * Flyway 数据库迁移配置
 * <p>
 * 支持 framework 和 business 两个独立的迁移管理。
 * </p>
 *
 * @author glowxq
 * @since 2024/7/29 13:30
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlywayConfiguration {

    private final DataSource dataSource;
    private final FlywayProperties flywayProperties;

    /**
     * 框架迁移
     */
    @Bean
    @Order(1)
    public Flyway frameworkFlyway() {
        FlywayProperties.FlywayConfig config = flywayProperties.getFramework();
        if (config == null || !config.isEnabled()) {
            log.info("Framework Flyway migration is disabled");
            return null;
        }

        log.info("Starting Framework Flyway migration from: {}", config.getLocations());

        Flyway flyway = Flyway.configure()
                              .dataSource(dataSource)
                              .locations(config.getLocations())
                              .table(config.getTable())
                              .baselineOnMigrate(config.isBaselineOnMigrate())
                              .baselineVersion(config.getBaselineVersion())
                              .validateOnMigrate(config.isValidateOnMigrate())
                              .load();

        migrateWithRepair(flyway, "framework");
        log.info("Framework Flyway migration completed");
        return flyway;
    }

    /**
     * 业务迁移
     */
    @Bean
    @Order(2)
    public Flyway businessFlyway() {
        FlywayProperties.FlywayConfig config = flywayProperties.getBusiness();
        if (config == null || !config.isEnabled()) {
            log.info("Business Flyway migration is disabled");
            return null;
        }

        log.info("Starting Business Flyway migration from: {}", config.getLocations());

        Flyway flyway = Flyway.configure()
                              .dataSource(dataSource)
                              .locations(config.getLocations())
                              .table(config.getTable())
                              .baselineOnMigrate(config.isBaselineOnMigrate())
                              .baselineVersion(config.getBaselineVersion())
                              .validateOnMigrate(config.isValidateOnMigrate())
                              .load();

        migrateWithRepair(flyway, "business");
        log.info("Business Flyway migration completed");
        return flyway;
    }

    private void migrateWithRepair(Flyway flyway, String scope) {
        try {
            // 先执行 repair 清理失败的迁移记录，然后再执行 migrate
            flyway.repair();
            flyway.migrate();
        } catch (FlywayValidateException exception) {
            log.warn("Flyway validation failed for {}, attempting repair before retrying.", scope, exception);
            flyway.repair();
            flyway.migrate();
        } catch (FlywaySqlException exception) {
            log.warn("Flyway SQL execution failed for {}, attempting repair before retrying.", scope, exception);
            flyway.repair();
            flyway.migrate();
        }
    }
}
