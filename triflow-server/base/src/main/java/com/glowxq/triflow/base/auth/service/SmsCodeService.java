package com.glowxq.triflow.base.auth.service;

/**
 * 短信验证码服务接口
 *
 * @author glowxq
 * @since 2025-01-26
 */
public interface SmsCodeService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型（login/register/reset）
     */
    void sendCode(String phone, String type);

    /**
     * 验证短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @param code  验证码
     * @return 是否验证通过
     */
    boolean verifyCode(String phone, String type, String code);

}
