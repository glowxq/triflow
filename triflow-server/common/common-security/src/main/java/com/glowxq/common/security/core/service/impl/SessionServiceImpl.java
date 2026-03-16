package com.glowxq.common.security.core.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.common.security.core.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 会话管理服务实现
 * <p>
 * 基于 Sa-Token 实现用户会话管理，包括踢用户下线等功能。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    @Override
    public void kickoutUser(Long userId) {
        if (userId == null) {
            return;
        }
        try {
            StpUtil.kickout(userId);
            log.info("用户 {} 已被踢下线", userId);
        } catch (Exception e) {
            log.warn("踢出用户 {} 失败: {}", userId, e.getMessage());
        }
    }

    @Override
    public void kickoutUsers(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        log.info("批量踢出 {} 个用户", userIds.size());
        for (Long userId : userIds) {
            kickoutUser(userId);
        }
    }

    @Override
    public int getOnlineUserCount() {
        try {
            // 获取所有已登录的会话数量
            return StpUtil.searchSessionId("", 0, -1, false).size();
        } catch (Exception e) {
            log.warn("获取在线用户数失败: {}", e.getMessage());
            return 0;
        }
    }
}
