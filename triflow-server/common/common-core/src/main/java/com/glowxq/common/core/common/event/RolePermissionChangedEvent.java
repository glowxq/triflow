package com.glowxq.common.core.common.event;

import lombok.Getter;

import java.util.List;

/**
 * 角色权限变更事件
 * <p>
 * 当角色的菜单权限被修改时发布此事件，用于通知系统踢出该角色下的所有用户。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
public class RolePermissionChangedEvent extends BaseEvent<Long> {

    /**
     * 受影响的用户ID列表
     */
    private final List<Long> affectedUserIds;

    /**
     * 创建角色权限变更事件
     *
     * @param source          事件源
     * @param roleId          角色ID
     * @param affectedUserIds 受影响的用户ID列表
     */
    public RolePermissionChangedEvent(Object source, Long roleId, List<Long> affectedUserIds) {
        super(source, roleId);
        this.affectedUserIds = affectedUserIds;
    }

    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    public Long getRoleId() {
        return getPayload();
    }
}
