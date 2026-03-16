package com.glowxq.common.core.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glowxq.common.core.common.enums.DataScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 登录用户信息
 * <p>
 * 封装当前登录用户的信息，包括基础信息、权限、角色、数据范围等。
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @since 2023/12/12
 */
@Data
public class LoginUser {

    @Schema(description = "基础用户信息")
    private BaseUserInfo userInfo;

    @Schema(description = "权限标识列表")
    private Set<String> permissionKeys = new HashSet<>();

    @Schema(description = "角色key列表")
    private Set<String> roleKeys = new HashSet<>();

    @Schema(description = "用户组id列表")
    private List<Long> groupIds = new ArrayList<>();

    @Schema(description = "子孙节点部门")
    private List<Long> deptAndChildren = new ArrayList<>();

    @Schema(description = "菜单id列表")
    private List<String> menuIds = new ArrayList<>();

    // ==================== 代理方法 ====================

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return userInfo != null ? userInfo.getId() : null;
    }


    /**
     * 获取用户部门ID
     */
    public Long getDeptId() {
        return userInfo != null ? userInfo.getDeptId() : null;
    }

    /**
     * 获取数据权限范围
     */
    public DataScopeEnum dataScope() {
        if (userInfo == null || userInfo.getDataScope() == null) {
            return null;
        }
        return userInfo.getDataScope();
    }

    /**
     * 获取角色列表
     */
    @JsonIgnore
    public List<String> getRoles() {
        return new ArrayList<>(this.roleKeys);
    }

    /**
     * 获取权限列表
     */
    @JsonIgnore
    public List<String> getPermissions() {
        return new ArrayList<>(this.permissionKeys);
    }
}
