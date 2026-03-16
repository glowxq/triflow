package com.glowxq.triflow.base;

import com.glowxq.common.core.common.configuration.AppConfig;
import com.glowxq.common.core.common.constant.Constant;
import com.glowxq.common.core.util.SpringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@ComponentScan(basePackages = {Constant.BASE_PACKAGE})
@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class BaseApplication {

    private static AppConfig appConfig;

    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
        String template = "------------------ [%s]  env:%s (v%s) -------------------";
        String result = String.format(template, appConfig.getName(), appConfig.getEnvironment(), appConfig.getVersion());
        System.out.println(result);
    }

    public void setAppConfig(AppConfig appConfig) {
        BaseApplication.appConfig = appConfig;
    }

    @PostConstruct
    public void init() {
        AppConfig appConfig = SpringUtils.getBean(AppConfig.class);
        setAppConfig(appConfig); // 通过辅助方法设置静态字段
    }

}
