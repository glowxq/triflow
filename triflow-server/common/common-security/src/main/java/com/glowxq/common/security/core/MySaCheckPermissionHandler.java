package com.glowxq.common.security.core;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import com.glowxq.common.security.core.util.LoginUtils;

import java.lang.reflect.AnnotatedElement;

/**
 * 自定义权限校验处理器
 * <p>
 * 在原有权限校验基础上，增加超级管理员跳过权限检查的功能。
 * </p>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
public class MySaCheckPermissionHandler implements SaAnnotationHandlerInterface<SaCheckPermission> {

    @Override
    public Class<SaCheckPermission> getHandlerAnnotationClass() {
        return SaCheckPermission.class;
    }

    public static void doCheck(String type, String[] value, SaMode mode, String[] orRole) {
        StpLogic stpLogic = SaManager.getStpLogic(type, false);

        try {
            if (mode == SaMode.AND) {
                stpLogic.checkPermissionAnd(value);
            } else {
                stpLogic.checkPermissionOr(value);
            }
        } catch (NotPermissionException e) {
            // 超级管理员跳过权限检查
            if (LoginUtils.isSuperAdmin()) {
                return;
            }
            // 权限认证校验未通过，再开始角色认证校验
            for (String role : orRole) {
                String[] rArr = SaFoxUtil.convertStringToArray(role);
                // 某一项 role 认证通过，则可以提前退出了，代表通过
                if (stpLogic.hasRoleAnd(rArr)) {
                    return;
                }
            }
            throw e;
        }
    }
    @Override
    public void checkMethod(SaCheckPermission at, AnnotatedElement element) {
        doCheck(at.type(), at.value(), at.mode(), at.orRole());
    }
}
