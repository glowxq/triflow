package com.glowxq.common.wechat.applet;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.glowxq.common.wechat.applet.exception.WechatLoginException;
import com.glowxq.common.wechat.applet.pojo.AuthInfoResult;
import com.glowxq.common.wechat.applet.pojo.WechatPhoneInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 微信小程序服务
 * <p>
 * 提供微信小程序相关的核心功能，包括：
 * <ul>
 *     <li>用户登录（code2session）</li>
 *     <li>手机号获取</li>
 *     <li>小程序码生成</li>
 * </ul>
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 用户登录
 * AuthInfoResult authInfo = wechatService.login(code);
 * String openId = authInfo.getOpenid();
 *
 * // 获取手机号
 * String phoneNumber = wechatService.getPhoneNumber(phoneCode);
 *
 * // 生成小程序码
 * File qrCodeFile = wechatService.createMiniAppCode("scene=123", "pages/index/index", "release");
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatService {

    private final WxMaService wxMaService;

    /**
     * 微信小程序登录
     * <p>
     * 通过登录凭证 code 获取用户的 openid 和 session_key。
     * </p>
     *
     * @param code 登录凭证（前端调用 wx.login() 获取）
     * @return 登录信息（包含 openid、session_key、unionid）
     * @throws WechatLoginException 登录失败时抛出业务异常
     */
    public AuthInfoResult login(String code) {
        WxMaUserService userService = wxMaService.getUserService();
        try {
            WxMaJscode2SessionResult sessionInfo = userService.getSessionInfo(code);

            return AuthInfoResult.builder()
                                 .openid(sessionInfo.getOpenid())
                                 .sessionKey(sessionInfo.getSessionKey())
                                 .unionId(sessionInfo.getUnionid())
                                 .build();
        } catch (WxErrorException e) {
            log.error("微信小程序登录失败，code: {}, 错误码: {}, 错误信息: {}", code, e.getError().getErrorCode(), e.getMessage(), e);
            // 根据微信错误码返回友好的错误信息
            String errorMsg = switch (e.getError().getErrorCode()) {
                case 40029 -> "登录凭证无效或已过期，请重新登录";
                case 45011 -> "登录频率过高，请稍后再试";
                case 40226 -> "高风险用户，请使用其他方式登录";
                case -1 -> "微信服务繁忙，请稍后再试";
                default -> "微信登录失败，请重试";
            };
            throw new WechatLoginException(errorMsg, e);
        }
    }

    /**
     * 获取用户手机号信息
     * <p>
     * 通过手机号获取凭证 code 换取用户手机号。
     * </p>
     *
     * @param phoneCode 手机号获取凭证（前端调用 getPhoneNumber 获取）
     * @return 手机号信息（包含完整手机号、纯数字手机号、国家代码）
     * @throws RuntimeException 获取失败时抛出
     */
    public WechatPhoneInfo getPhoneInfo(String phoneCode) {
        if (phoneCode == null || phoneCode.isBlank()) {
            return null;
        }

        WxMaUserService userService = wxMaService.getUserService();
        try {
            WxMaPhoneNumberInfo phoneNumberInfo = userService.getPhoneNumber(phoneCode);

            return WechatPhoneInfo.builder()
                                  .phoneNumber(phoneNumberInfo.getPhoneNumber())
                                  .purePhoneNumber(phoneNumberInfo.getPurePhoneNumber())
                                  .countryCode(phoneNumberInfo.getCountryCode())
                                  .build();
        } catch (WxErrorException e) {
            log.error("获取微信手机号失败，phoneCode: {}, 错误: {}", phoneCode, e.getMessage(), e);
            throw new RuntimeException("获取微信手机号失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户手机号
     * <p>
     * 简化版本，只返回手机号字符串。
     * </p>
     *
     * @param phoneCode 手机号获取凭证
     *
     * @return 手机号，获取失败返回 null
     */
    public String getPhoneNumber(String phoneCode) {
        WechatPhoneInfo phoneInfo = getPhoneInfo(phoneCode);
        return phoneInfo != null ? phoneInfo.getPhoneNumber() : null;
    }

    /**
     * 生成小程序码（无限制）
     * <p>
     * 生成永久有效的小程序码，数量无限制，但场景值长度有限。
     * </p>
     *
     * @param scene      场景值（最大 32 字符）
     * @param page       小程序页面路径（如 pages/index/index）
     * @param envVersion 环境版本：release（正式版）、trial（体验版）、develop（开发版）
     *
     * @return 小程序码图片文件
     *
     * @throws RuntimeException 生成失败时抛出
     */
    public File createMiniAppCode(String scene, String page, String envVersion) {
        WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();
        try {
            return qrcodeService.createWxaCodeUnlimit(scene, page, false, envVersion, 430, true, null, false);
        } catch (WxErrorException e) {
            log.error("生成小程序码失败，scene: {}, page: {}, 错误: {}", scene, page, e.getMessage(), e);
            throw new RuntimeException("生成小程序码失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成小程序码（简化版）
     * <p>
     * 使用默认配置生成正式版小程序码。
     * </p>
     *
     * @param scene 场景值
     * @param page  小程序页面路径
     *
     * @return 小程序码图片文件
     */
    public File createMiniAppCode(String scene, String page) {
        return createMiniAppCode(scene, page, "release");
    }

    /**
     * 获取 WxMaService 实例
     * <p>
     * 供高级用户直接使用原生 API。
     * </p>
     *
     * @return WxMaService 实例
     */
    public WxMaService getWxMaService() {
        return wxMaService;
    }
}
