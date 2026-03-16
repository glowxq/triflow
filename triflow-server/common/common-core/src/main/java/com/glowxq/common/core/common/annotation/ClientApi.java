package com.glowxq.common.core.common.annotation;

import java.lang.annotation.*;

/**
 * 客户端 API 标识
 * <p>
 * 标识客户端接口，仅需用户登录认证，无需权限校验。
 * </p>
 *
 * @author glowxq
 * @since 2026-02-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ClientApi {

    /**
     * API 描述（可选）
     */
    String value() default "";
}
