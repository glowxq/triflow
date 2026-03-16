package com.glowxq.triflow.base.auth.service;

import com.glowxq.triflow.base.auth.pojo.dto.FeishuUserDTO;

/**
 * 飞书服务接口
 * <p>
 * 提供飞书 OAuth 登录相关的功能，包括获取授权 URL、获取用户信息等。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
public interface FeishuService {

    /**
     * 获取飞书授权 URL
     *
     * @param redirectUri 回调地址
     * @param state       状态码，用于防止 CSRF 攻击
     * @return 授权 URL
     */
    String getAuthUrl(String redirectUri, String state);

    /**
     * 通过授权码获取用户信息
     *
     * @param code 飞书授权码
     * @return 飞书用户信息
     */
    FeishuUserDTO getUserInfo(String code);

    /**
     * 获取应用访问令牌
     *
     * @return 应用访问令牌 (app_access_token)
     */
    String getAppAccessToken();

}
