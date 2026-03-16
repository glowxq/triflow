package com.glowxq.common.core.common.annotation;

import java.lang.annotation.*;

/**
 * 管理端 API 标识
 * <p>
 * 标识管理后台接口，需要：
 * 1. 用户登录认证
 * 2. 方法级权限控制（{@code @SaCheckPermission}）
 * </p>
 *
 * @author glowxq
 * @since 2026-02-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AdminApi {

    /**
     * API 描述（可选）
     */
    String value() default "";
}
