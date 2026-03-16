package com.glowxq.common.core.common.annotation;

import java.lang.annotation.*;

/**
 * 匿名 API 标识
 * <p>
 * 标识公开接口，无需登录和权限校验。
 * 对应路由需在白名单中配置。
 * </p>
 *
 * @author glowxq
 * @since 2026-02-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AnonApi {

    /**
     * API 描述（可选）
     */
    String value() default "";
}
