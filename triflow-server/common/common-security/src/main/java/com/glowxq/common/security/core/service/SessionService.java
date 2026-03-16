package com.glowxq.common.security.core.service;

import java.util.List;

/**
 * 会话管理服务接口
 * <p>
 * 提供用户会话管理功能，包括踢用户下线、获取在线用户数等。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
public interface SessionService {

    /**
     * 踢出单个用户（强制下线）
     *
     * @param userId 用户ID
     */
    void kickoutUser(Long userId);

    /**
     * 批量踢出用户（强制下线）
     *
     * @param userIds 用户ID列表
     */
    void kickoutUsers(List<Long> userIds);

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数
     */
    int getOnlineUserCount();
}
