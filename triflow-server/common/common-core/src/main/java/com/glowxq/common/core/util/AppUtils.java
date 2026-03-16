package com.glowxq.common.core.util;

import com.glowxq.common.core.common.configuration.AppConfig;
import com.glowxq.common.core.common.enums.EnvEnum;

/**
 * 应用程序使用情况
 *
 * @author glowxq
 * @date 2024/03/01
 */
public class AppUtils {

    /**
     * 应用配置
     */
    private static final AppConfig APP_CONFIG = SpringUtils.getBean(AppConfig.class);

    /**
     * 获取版本
     *
     * @return
     */
    public static String getVersion() {
        return APP_CONFIG.getVersion();
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName() {
        return APP_CONFIG.getName();
    }

    /**
     * 获取业务
     *
     * @return {@link String }
     */
    public static String getBusinessPath() {
        return APP_CONFIG.getBusiness();
    }

    /**
     * 获取业务api前缀
     * @return
     */
    public static String getBusinessApiPrefix() {
        return StringUtils.upperCase(APP_CONFIG.getBusiness());
    }

    /**
     * 非生产环境
     *
     * @return {@link Boolean }
     */
    public static Boolean isNotProd() {
        return !isProd();
    }

    /**
     * 生产环境
     *
     * @return {@link Boolean }
     */
    public static Boolean isProd() {
        EnvEnum envEnum = getEnvironment();
        return envEnum.equals(EnvEnum.Prod);
    }

    /**
     * 开发环境
     *
     * @return {@link Boolean }
     */
    public static Boolean isDev() {
        EnvEnum envEnum = getEnvironment();
        return envEnum.equals(EnvEnum.Dev);
    }

    /**
     * 获取环境
     *
     * @return {@link String }
     */
    public static EnvEnum getEnvironment() {
        return EnvEnum.matchCode(APP_CONFIG.getEnvironment());
    }

    public static boolean isLocal() {
        return getEnvironment().equals(EnvEnum.Local);
    }

    public static boolean isLocalOrDev(){
        return isLocal() || isDev();
    }
}
