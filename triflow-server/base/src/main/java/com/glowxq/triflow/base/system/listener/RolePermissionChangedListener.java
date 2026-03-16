package com.glowxq.triflow.base.system.listener;

import com.glowxq.common.core.common.event.RolePermissionChangedEvent;
import com.glowxq.common.security.core.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 角色权限变更事件监听器
 * <p>
 * 监听角色权限变更事件，当角色的菜单权限被修改时，
 * 自动踢出该角色下的所有用户，使其重新登录以获取最新权限。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RolePermissionChangedListener {

    private final SessionService sessionService;

    /**
     * 处理角色权限变更事件
     * <p>
     * 异步执行，避免阻塞主流程。
     * </p>
     *
     * @param event 角色权限变更事件
     */
    @Async
    @EventListener
    public void handleRolePermissionChanged(RolePermissionChangedEvent event) {
        if (CollectionUtils.isEmpty(event.getAffectedUserIds())) {
            log.debug("角色 {} 权限变更，无受影响用户", event.getRoleId());
            return;
        }

        log.info("角色 {} 权限变更，踢出 {} 个受影响用户",
                event.getRoleId(), event.getAffectedUserIds().size());

        sessionService.kickoutUsers(event.getAffectedUserIds());
    }
}
