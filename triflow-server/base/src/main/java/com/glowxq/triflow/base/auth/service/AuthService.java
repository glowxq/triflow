package com.glowxq.triflow.base.auth.service;

import com.glowxq.triflow.base.auth.pojo.dto.BindSocialDTO;
import com.glowxq.triflow.base.auth.pojo.dto.BindWechatPhoneDTO;
import com.glowxq.triflow.base.auth.pojo.dto.LoginDTO;
import com.glowxq.triflow.base.auth.pojo.dto.ProfileUpdateDTO;
import com.glowxq.triflow.base.auth.pojo.dto.RegisterDTO;
import com.glowxq.triflow.base.auth.pojo.dto.UpdatePasswordDTO;
import com.glowxq.triflow.base.auth.pojo.vo.LoginVO;
import com.glowxq.triflow.base.auth.pojo.vo.UserInfoVO;
import com.glowxq.triflow.base.auth.pojo.vo.UserSocialVO;

import java.util.List;

/**
 * 认证服务接口
 * <p>
 * 提供登录、登出、刷新Token等认证相关功能。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
public interface AuthService {

    /**
     * 统一登录入口
     *
     * @param dto 登录请求参数
     * @return 登录响应（含token和用户信息）
     */
    LoginVO login(LoginDTO dto);

    /**
     * 登出
     */
    void logout();

    /**
     * 刷新Token
     *
     * @return 新的登录响应
     */
    LoginVO refreshToken();

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoVO getUserInfo();

    /**
     * 获取当前用户权限码列表
     *
     * @return 权限码列表
     */
    List<String> getPermissionCodes();

    /**
     * 更新当前用户个人资料
     *
     * @param dto 更新参数
     */
    void updateProfile(ProfileUpdateDTO dto);

    /**
     * 修改当前用户密码
     *
     * @param dto 密码更新参数
     */
    void updatePassword(UpdatePasswordDTO dto);

    /**
     * 获取当前用户的第三方账号绑定列表
     *
     * @return 绑定列表
     */
    List<UserSocialVO> getUserSocials();

    /**
     * 绑定飞书账号
     *
     * @param dto 绑定参数（包含授权码和是否强制绑定）
     */
    void bindFeishu(BindSocialDTO dto);

    /**
     * 解绑第三方账号
     *
     * @param socialType 第三方平台类型
     */
    void unbindSocial(String socialType);

    /**
     * 绑定微信手机号
     * <p>
     * 使用微信小程序获取手机号的授权码，调用微信接口获取手机号并绑定到当前用户
     * </p>
     *
     * @param dto 绑定参数（包含微信授权码）
     */
    void bindWechatPhone(BindWechatPhoneDTO dto);

    /**
     * 用户注册
     * <p>
     * 使用手机号+短信验证码注册
     * </p>
     *
     * @param dto 注册参数
     * @return 注册成功后的登录响应
     */
    LoginVO register(RegisterDTO dto);

}
