package com.glowxq.common.security.core.util;

import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.common.core.common.constant.GlobalConstant;
import com.glowxq.common.core.common.entity.BaseUserInfo;
import com.glowxq.common.core.common.entity.LoginUser;
import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.exception.common.AlertsException;
import com.glowxq.common.core.common.exception.common.ClientException;
import com.glowxq.common.core.util.StringUtils;
import com.glowxq.common.security.core.constants.RoleConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * 登录工具类
 * <p>
 * 提供登录相关的静态工具方法，包括登录、获取用户信息、权限检查等。
 * </p>
 *
 * @author glowxq
 * @version 1.1
 * @since 2024/1/24
 */
@Slf4j
public class LoginUtils {

    public static final String USER_KEY = "loginUser";

    private LoginUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 执行登录
     *
     * @param loginUser 登录用户信息
     * @param model     登录模型配置
     * @param extraData 额外数据
     */
    public static void performLogin(LoginUser loginUser, SaLoginModel model, Map<String, Object> extraData) {
        model = model != null ? model : new SaLoginModel();
        model.setExtraData(extraData);
        StpUtil.login(loginUser.getUserInfo().getId(), model);
        StpUtil.getTokenSession().set(USER_KEY, loginUser);
        StpUtil.getTokenSession().updateTimeout(model.getTimeout());
        StpUtil.getSession().updateTimeout(model.getTimeout());
    }

    /**
     * 执行小程序登录
     *
     * @param userId    用户ID
     * @param loginUser 登录用户信息
     * @param model     登录模型配置
     * @param extraData 额外数据
     */
    public static void performAppletLogin(Object userId, Object loginUser, SaLoginModel model, Map<String, Object> extraData) {
        model = model != null ? model : new SaLoginModel();
        model.setExtraData(extraData);
        StpUtil.login(userId, model);
        StpUtil.getTokenSession().set(USER_KEY, loginUser);
        StpUtil.getTokenSession().updateTimeout(model.getTimeout());
        StpUtil.getSession().updateTimeout(model.getTimeout());
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户信息
     * @throws AlertsException 如果用户未登录
     */
    public static LoginUser getLoginUser() {
        SaSession session = null;
        try {
            session = StpUtil.getTokenSession();
        } catch (SaJsonConvertException e) {
            StpUtil.logout();
        }
        if (session == null) {
            throw new ClientException(ErrorCodeEnum.TOKEN_EXPIRED);
        }
        return (LoginUser) session.get(USER_KEY);
    }

    /**
     * 根据Token获取登录用户
     *
     * @param token 访问令牌
     * @return 登录用户信息，未找到返回null
     */
    public static LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (session == null) {
            return null;
        }
        return (LoginUser) session.get(USER_KEY);
    }

    /**
     * 检查用户是否未登录
     *
     * @return true表示未登录，false表示已登录
     */
    public static boolean isNotLogin() {
        try {
            return !StpUtil.isLogin();
        } catch (NotWebContextException e) {
            log.debug("非Web环境，返回未登录状态");
            return true;
        } catch (Exception e) {
            log.error("检查登录状态时发生异常: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 检查用户是否已登录
     *
     * @return true表示已登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID，未登录返回 null
     */
    public static Long getUserId() {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        return loginId != null ? Long.valueOf(loginId.toString()) : null;
    }

    /**
     * 判断是否为超级管理员
     *
     * @return true表示是超级管理员
     */
    public static boolean isSuperAdmin() {
        if (!StpUtil.isLogin()) {
            return false;
        }
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return loginUser.getRoleKeys().contains(GlobalConstant.SUPER_ROLE);
    }

    /**
     * 检查是否拥有任一权限
     * <p>
     * 使用 Sa-Token 内置方法，会调用 StpInterface 实现并利用缓存机制。
     * </p>
     *
     * @param permissions 权限标识列表
     * @return true 表示拥有其中至少一个权限
     */
    public static boolean hasAnyPermissions(String... permissions) {
        if (!StpUtil.isLogin() || permissions == null || permissions.length == 0) {
            return false;
        }
        return StpUtil.hasPermissionOr(permissions);
    }

    /**
     * 检查是否拥有任一角色
     * <p>
     * 使用 Sa-Token 内置方法，会调用 StpInterface 实现并利用缓存机制。
     * </p>
     *
     * @param roleKeys 角色标识列表
     * @return true 表示拥有其中至少一个角色
     */
    public static boolean hasAnyRoles(String... roleKeys) {
        if (!StpUtil.isLogin() || roleKeys == null || roleKeys.length == 0) {
            return false;
        }
        return StpUtil.hasRoleOr(roleKeys);
    }

    /**
     * 判断是否为管理员
     *
     * @return true表示是管理员
     */
    public static Boolean isAdmin() {
        return hasAnyRoles(RoleConstant.ROOT, RoleConstant.ADMIN);
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名，未登录返回空字符串
     */
    public static String getUsername() {
        return Optional.ofNullable(getLoginUser())
                       .map(LoginUser::getUserInfo)
                       .map(BaseUserInfo::getUsername)
                       .orElse(StringUtils.EMPTY);
    }

    /**
     * 获取当前用户基础信息
     *
     * @return 用户基础信息
     */
    public static BaseUserInfo getUserInfo() {
        return Optional.ofNullable(getLoginUser())
                       .map(LoginUser::getUserInfo)
                       .orElse(new BaseUserInfo());
    }

    /**
     * 获取当前用户部门ID
     *
     * @return 部门ID
     */
    public static Long getDeptId() {
        return LoginUtils.getLoginUser().getUserInfo().getDeptId();
    }
}
