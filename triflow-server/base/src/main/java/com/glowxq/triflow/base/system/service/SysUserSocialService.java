package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.entity.SysUserSocial;

import java.util.List;

/**
 * 用户第三方账号服务接口
 * <p>
 * 定义第三方账号关联业务逻辑的契约。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
public interface SysUserSocialService {

    /**
     * 根据第三方平台类型和平台用户ID查询关联信息
     *
     * @param socialType 第三方平台类型
     * @param socialId   第三方平台用户ID（openid）
     * @return 关联信息
     */
    SysUserSocial getBySocialTypeAndId(String socialType, String socialId);

    /**
     * 根据用户ID和第三方平台类型查询
     *
     * @param userId     用户ID
     * @param socialType 第三方平台类型
     * @return 关联信息
     */
    SysUserSocial getByUserIdAndType(Long userId, String socialType);

    /**
     * 根据用户ID查询所有第三方账号关联
     *
     * @param userId 用户ID
     * @return 关联列表
     */
    List<SysUserSocial> getByUserId(Long userId);

    /**
     * 创建或更新第三方账号关联
     *
     * @param social 关联信息
     * @return 关联ID
     */
    Long saveOrUpdate(SysUserSocial social);

    /**
     * 解除第三方账号绑定
     *
     * @param userId     用户ID
     * @param socialType 第三方平台类型
     * @return 是否成功
     */
    boolean unbind(Long userId, String socialType);

}
