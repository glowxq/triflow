package com.glowxq.common.core.common.configuration;

import com.glowxq.common.core.common.enums.EnvEnum;
import com.glowxq.common.core.util.AppUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: firebug-java
 * @description:
 * @author: glowxq
 * @create: 2023-08-28 01:22
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "feishu.robot.webhook")
public class FeiShuConfig {

    /**
     * 开发内部 url
     */
    private String internalUrl;

    /**
     * 业务
     */
    private String businessUrl;

    /**
     * glowxq
     */
    private String glowxqUrl;

    /**
     * 不是生产
     *
     * @return boolean
     */
    public boolean isNotProd() {
        return AppUtils.isNotProd();
    }

    public EnvEnum env() {
        return AppUtils.getEnvironment();
    }
}
