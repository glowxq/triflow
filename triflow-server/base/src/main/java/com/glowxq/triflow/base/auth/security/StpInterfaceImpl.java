package com.glowxq.triflow.base.auth.security;

import cn.dev33.satoken.stp.StpInterface;
import com.glowxq.common.core.common.entity.LoginUser;
import com.glowxq.common.core.common.constant.GlobalConstant;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.system.service.SysMenuService;
import com.glowxq.triflow.base.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Sa-Token 权限接口实现
 * <p>
 * 实现 Sa-Token 的 StpInterface 接口，用于获取用户权限和角色信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserService userService;
    private final SysMenuService menuService;

    /**
     * 获取指定用户的权限码列表
     *
     * @param loginId   用户ID
     * @param loginType 登录类型
     * @return 权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 首先尝试从缓存（Session）获取
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser != null && CollectionUtils.isNotEmpty(loginUser.getPermissionKeys())) {
            return new ArrayList<>(loginUser.getPermissionKeys());
        }

        // 从数据库获取
        Long userId = Long.valueOf(loginId.toString());

        // 超级管理员拥有所有权限
        Set<String> roleCodes = userService.getUserRoleCodes(userId);
        if (roleCodes.contains(GlobalConstant.SUPER_ROLE)) {
            return menuService.getAllPermissions();
        }

        // 普通用户根据角色获取权限
        Set<String> permissions = userService.getUserPermissions(userId);
        return new ArrayList<>(permissions);
    }

    /**
     * 获取指定用户的角色列表
     *
     * @param loginId   用户ID
     * @param loginType 登录类型
     * @return 角色编码列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 首先尝试从缓存（Session）获取
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser != null && CollectionUtils.isNotEmpty(loginUser.getRoleKeys())) {
            return new ArrayList<>(loginUser.getRoleKeys());
        }

        // 从数据库获取
        Long userId = Long.valueOf(loginId.toString());
        Set<String> roleCodes = userService.getUserRoleCodes(userId);
        return new ArrayList<>(roleCodes);
    }

}
