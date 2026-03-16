package com.glowxq.triflow.base.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.common.core.common.entity.BaseUserInfo;
import com.glowxq.common.core.common.entity.LoginUser;
import com.glowxq.common.core.common.enums.DataScopeEnum;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.common.security.enums.GrantType;
import com.glowxq.triflow.base.auth.pojo.dto.BindSocialDTO;
import com.glowxq.triflow.base.auth.pojo.dto.BindWechatPhoneDTO;
import com.glowxq.triflow.base.auth.pojo.dto.LoginDTO;
import com.glowxq.triflow.base.auth.pojo.dto.ProfileUpdateDTO;
import com.glowxq.triflow.base.auth.pojo.dto.RegisterDTO;
import com.glowxq.triflow.base.auth.pojo.dto.UpdatePasswordDTO;
import com.glowxq.triflow.base.auth.service.SmsCodeService;
import com.glowxq.triflow.base.captcha.service.CaptchaService;
import com.glowxq.triflow.base.auth.pojo.vo.LoginVO;
import com.glowxq.triflow.base.auth.pojo.vo.UserInfoVO;
import com.glowxq.triflow.base.auth.pojo.vo.UserSocialVO;
import com.glowxq.triflow.base.auth.service.AuthService;
import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.entity.SysUserSocial;
import com.glowxq.triflow.base.system.pojo.dto.SysUserCreateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserDetailVO;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.service.SysSwitchService;
import com.glowxq.triflow.base.auth.pojo.dto.FeishuUserDTO;
import com.glowxq.triflow.base.auth.service.FeishuService;
import com.glowxq.triflow.base.system.service.SysDeptService;
import com.glowxq.triflow.base.system.service.SysUserService;
import com.glowxq.triflow.base.system.service.SysUserSocialService;
import com.glowxq.common.core.common.enums.SocialType;
import com.glowxq.triflow.base.system.enums.SwitchKeyEnum;
import com.glowxq.triflow.base.system.enums.ConfigKeyEnum;
import com.glowxq.triflow.base.system.service.SysConfigService;
import com.glowxq.common.wechat.applet.WechatService;
import com.glowxq.common.wechat.applet.pojo.AuthInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 认证服务实现类
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService userService;
    private final SysUserSocialService userSocialService;
    private final SysDeptService deptService;
    private final SysSwitchService switchService;
    private final SysConfigService configService;
    private final FeishuService feishuService;
    private final WechatService wechatService;
    private final SmsCodeService smsCodeService;
    private final CaptchaService captchaService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginDTO dto) {
        GrantType grantType = GrantType.matchCode(dto.getGrantType());
        if (grantType == null) {
            throw new BusinessException("不支持的授权类型: " + dto.getGrantType());
        }

        // 检查验证码开关（仅密码登录需要验证码）
        if (grantType == GrantType.password && switchService.isEnabled(SwitchKeyEnum.SECURITY_CAPTCHA)) {
            validateCaptcha(dto);
        }

        // 手机号登录开关
        if (grantType == GrantType.sms && !switchService.isEnabled(SwitchKeyEnum.USER_LOGIN_PHONE)) {
            throw new BusinessException("手机号登录已关闭");
        }

        // 第三方登录开关
        if (isSocialGrantType(grantType) && !switchService.isEnabled(SwitchKeyEnum.USER_LOGIN_SOCIAL)) {
            throw new BusinessException("第三方登录已关闭");
        }

        SysUser user;
        switch (grantType) {
            case password -> user = loginByPassword(dto);
            case sms -> user = loginBySms(dto);
            case wechat_miniapp -> user = loginByWechat(dto);
            case third -> user = loginByThird(dto);
            case feishu -> user = loginByFeishu(dto);
            case test -> user = loginByTest(dto);
            default -> throw new BusinessException("不支持的授权类型: " + dto.getGrantType());
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用或锁定");
        }

        // 构建登录用户
        LoginUser loginUser = buildLoginUser(user);

        // 执行登录
        SaLoginModel model = new SaLoginModel();
        model.setDevice("pc");
        model.setTimeout(resolveTokenTimeoutSeconds());
        LoginUtils.performLogin(loginUser, model, null);

        // 更新登录信息
        userService.updateLoginInfo(user.getId(), null);

        // 构建响应并返回
        // 注：手机号收集逻辑由前端处理，前端登录后检查开关状态和用户信息
        return buildLoginVO(user, loginUser);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public LoginVO refreshToken() {
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("用户未登录");
        }

        // 重新加载用户信息
        SysUser user = userService.getByUsername(loginUser.getUserInfo().getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 重新构建登录用户
        LoginUser newLoginUser = buildLoginUser(user);

        // 刷新Token
        SaLoginModel model = new SaLoginModel();
        model.setDevice("pc");
        model.setTimeout(resolveTokenTimeoutSeconds());
        LoginUtils.performLogin(newLoginUser, model, null);

        return buildLoginVO(user, newLoginUser);
    }

    @Override
    public UserInfoVO getUserInfo() {
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("用户未登录");
        }

        BaseUserInfo baseInfo = loginUser.getUserInfo();
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(baseInfo.getId());
        vo.setUsername(baseInfo.getUsername());
        vo.setRealName(baseInfo.getName());
        vo.setNickname(baseInfo.getNickname());
        vo.setAvatar(baseInfo.getAvatar());
        vo.setPhone(baseInfo.getPhone());
        vo.setEmail(baseInfo.getEmail());
        vo.setRoles(new ArrayList<>(loginUser.getRoleKeys()));

        // 获取首页路径及密码状态
        SysUser user = userService.getByUsername(baseInfo.getUsername());
        if (user != null) {
            if (StringUtils.isNotBlank(user.getHomePath())) {
                vo.setHomePath(user.getHomePath());
            } else {
                vo.setHomePath("/dashboard");
            }
            vo.setPasswordSet(user.getPasswordSet() != null && user.getPasswordSet() == 1);
            vo.setNickname(StringUtils.defaultIfBlank(user.getNickname(), vo.getNickname()));
            vo.setAvatar(StringUtils.defaultIfBlank(user.getAvatar(), vo.getAvatar()));
            vo.setPhone(StringUtils.defaultIfBlank(user.getPhone(), vo.getPhone()));
            vo.setEmail(StringUtils.defaultIfBlank(user.getEmail(), vo.getEmail()));
            vo.setGender(user.getGender());
            vo.setDesc(user.getIntroduction());
            // 积分
            vo.setPoints(user.getPoints() != null ? user.getPoints() : 0L);
            vo.setFrozenPoints(user.getFrozenPoints() != null ? user.getFrozenPoints() : 0L);
            vo.setRewardPoints(user.getRewardPoints() != null ? user.getRewardPoints() : 0L);
        } else {
            vo.setHomePath("/dashboard");
            vo.setPasswordSet(true);
            vo.setPoints(0L);
            vo.setFrozenPoints(0L);
            vo.setRewardPoints(0L);
        }

        return vo;
    }

    @Override
    public List<String> getPermissionCodes() {
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("用户未登录");
        }
        Set<String> permissions = userService.getUserPermissions(loginUser.getUserInfo().getId());
        return new ArrayList<>(permissions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(ProfileUpdateDTO dto) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 获取当前用户
        SysUserDetailVO userVO = userService.getById(userId);
        if (userVO == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        SysUser user = new SysUser();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setRealName(dto.getRealName());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setIntroduction(dto.getIntroduction());
        user.setUpdateTime(LocalDateTime.now());

        userService.updateById(user);
        log.info("更新用户资料, userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordDTO dto) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        if (!StringUtils.equals(dto.getNewPassword(), dto.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException("用户未登录");
        }

        SysUser user = userService.getByUsername(loginUser.getUserInfo().getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        boolean passwordSet = user.getPasswordSet() != null && user.getPasswordSet() == 1;
        if (passwordSet) {
            if (StringUtils.isBlank(dto.getOldPassword())) {
                throw new BusinessException("旧密码不能为空");
            }
            if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException("旧密码不正确");
            }
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        updateUser.setPasswordSet(1);
        updateUser.setUpdateTime(LocalDateTime.now());
        userService.updateById(updateUser);
        log.info("用户修改密码成功, userId={}", userId);
    }

    @Override
    public List<UserSocialVO> getUserSocials() {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        List<SysUserSocial> socials = userSocialService.getByUserId(userId);
        return socials.stream().map(social -> {
            UserSocialVO vo = new UserSocialVO();
            vo.setId(social.getId());
            vo.setSocialType(social.getSocialType());
            vo.setNickname(social.getNickname());
            vo.setAvatar(social.getAvatar());
            vo.setBindTime(social.getBindTime());
            return vo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindFeishu(BindSocialDTO dto) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 通过授权码获取飞书用户信息
        FeishuUserDTO feishuUser = feishuService.getUserInfo(dto.getCode());
        if (feishuUser == null || StringUtils.isBlank(feishuUser.getOpenId())) {
            throw new BusinessException("获取飞书用户信息失败");
        }

        String socialTypeCode = SocialType.FEISHU.getCode();
        String socialId = feishuUser.getOpenId();

        // 检查是否已被其他用户绑定
        SysUserSocial existingSocial = userSocialService.getBySocialTypeAndId(socialTypeCode, socialId);
        if (existingSocial != null) {
            if (existingSocial.getUserId().equals(userId)) {
                throw new BusinessException("该飞书账号已绑定到当前用户");
            }
            if (!Boolean.TRUE.equals(dto.getForce())) {
                throw new BusinessException("该飞书账号已被其他用户绑定，如需强制绑定请设置 force=true");
            }
            // 强制绑定：解除原用户的绑定
            userSocialService.unbind(existingSocial.getUserId(), socialTypeCode);
            log.info("强制绑定：解除原用户绑定, 原userId={}, socialId={}", existingSocial.getUserId(), socialId);
        }

        // 检查当前用户是否已绑定飞书
        List<SysUserSocial> currentSocials = userSocialService.getByUserId(userId);
        boolean alreadyBound = currentSocials.stream()
                .anyMatch(s -> socialTypeCode.equals(s.getSocialType()));
        if (alreadyBound) {
            throw new BusinessException("当前用户已绑定飞书账号，请先解绑");
        }

        // 绑定新账号
        SysUserSocial social = new SysUserSocial();
        social.setUserId(userId);
        social.setSocialType(socialTypeCode);
        social.setSocialId(socialId);
        social.setUnionId(feishuUser.getUnionId());
        social.setNickname(feishuUser.getName());
        social.setAvatar(feishuUser.getAvatarUrl());
        social.setBindTime(LocalDateTime.now());
        userSocialService.saveOrUpdate(social);

        log.info("绑定飞书账号成功, userId={}, openId={}, name={}", userId, socialId, feishuUser.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindSocial(String socialType) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        boolean success = userSocialService.unbind(userId, socialType);
        if (!success) {
            throw new BusinessException("解绑失败，可能未绑定该平台账号");
        }
        log.info("解绑第三方账号成功, userId={}, socialType={}", userId, socialType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindWechatPhone(BindWechatPhoneDTO dto) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        // 调用微信接口获取手机号
        String phoneNumber = wechatService.getPhoneNumber(dto.getCode());
        if (StringUtils.isBlank(phoneNumber)) {
            throw new BusinessException("获取手机号失败，请重新授权");
        }

        // 检查手机号是否已被其他用户使用
        SysUser existingUser = userService.getByPhone(phoneNumber);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new BusinessException("该手机号已被其他账号绑定");
        }

        // 更新用户手机号
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPhone(phoneNumber);
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);

        log.info("绑定微信手机号成功, userId={}, phone={}****{}", userId,
                phoneNumber.substring(0, 3), phoneNumber.substring(7));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO dto) {
        // 检查注册开关
        if (!switchService.isEnabled(SwitchKeyEnum.USER_REGISTER)) {
            throw new BusinessException("当前系统暂不开放注册");
        }

        // 校验密码一致性
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 校验短信验证码
        boolean verified = smsCodeService.verifyCode(dto.getPhone(), "register", dto.getSmsCode());
        if (!verified) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 检查手机号是否已注册
        SysUser existingUser = userService.getByPhone(dto.getPhone());
        if (existingUser != null) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建用户
        SysUserCreateDTO createDTO = new SysUserCreateDTO();
        createDTO.setUsername(dto.getPhone()); // 使用手机号作为用户名
        createDTO.setPhone(dto.getPhone());
        createDTO.setPassword(dto.getPassword());
        createDTO.setNickname(StringUtils.isNotBlank(dto.getNickname()) ? dto.getNickname() : "用户" + dto.getPhone().substring(7));
        createDTO.setAvatar(dto.getAvatar());
        createDTO.setStatus(1);
        createDTO.setRoleIds(List.of(DEFAULT_USER_ROLE_ID));

        Long userId = userService.create(createDTO);
        log.info("用户注册成功, userId={}, phone={}", userId, maskPhone(dto.getPhone()));

        // 获取用户信息并自动登录
        SysUser user = userService.getByUsername(dto.getPhone());
        LoginUser loginUser = buildLoginUser(user);

        // 执行登录
        SaLoginModel model = new SaLoginModel();
        model.setDevice("pc");
        model.setTimeout(resolveTokenTimeoutSeconds());
        LoginUtils.performLogin(loginUser, model, null);

        return buildLoginVO(user, loginUser);
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }


    /**
     * 校验验证码
     *
     * @param captchaKey  验证码Key
     * @param captchaCode 验证码
     */
    private void validateCaptcha(LoginDTO dto) {
        if (dto == null) {
            throw new BusinessException("验证码信息不能为空");
        }

        String captchaKey = dto.getCaptchaKey();
        String captchaCode = dto.getCaptchaCode();

        if (StringUtils.isNotBlank(captchaKey) || StringUtils.isNotBlank(captchaCode)) {
            if (StringUtils.isBlank(captchaKey)) {
                throw new BusinessException("验证码Key不能为空");
            }
            if (StringUtils.isBlank(captchaCode)) {
                throw new BusinessException("验证码不能为空");
            }

            boolean verified = captchaService.verifyCaptcha(captchaKey, captchaCode);
            if (!verified) {
                throw new BusinessException("验证码错误或已过期");
            }
            return;
        }

        if (Boolean.TRUE.equals(dto.getCaptcha())) {
            return;
        }

        throw new BusinessException("验证码Key不能为空");
    }

    /**
     * 判断是否为第三方登录
     */
    private boolean isSocialGrantType(GrantType grantType) {
        return grantType == GrantType.wechat_miniapp
                || grantType == GrantType.third
                || grantType == GrantType.feishu
                || grantType == GrantType.google;
    }

    /**
     * 解析 Token 有效期（秒）
     */
    private long resolveTokenTimeoutSeconds() {
        String value = configService.getValueByKey(ConfigKeyEnum.SYS_TOKEN_EXPIRE_TIME.getKey(), "1440");
        long minutes = NumberUtils.toLong(value, 1440L);
        if (minutes <= 0) {
            minutes = 1440L;
        }
        return minutes * 60;
    }

    // ==================== 私有方法 ====================

    /**
     * 账号密码登录
     */
    private SysUser loginByPassword(LoginDTO dto) {
        if (StringUtils.isBlank(dto.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        SysUser user = userService.getByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        return user;
    }

    /**
     * 手机号登录（静默注册）
     */
    @Transactional(rollbackFor = Exception.class)
    protected SysUser loginBySms(LoginDTO dto) {
        if (StringUtils.isBlank(dto.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }
        if (StringUtils.isBlank(dto.getSmsCode())) {
            throw new BusinessException("验证码不能为空");
        }

        // 校验短信验证码
        boolean verified = smsCodeService.verifyCode(dto.getPhone(), "login", dto.getSmsCode());
        if (!verified) {
            throw new BusinessException("验证码错误或已过期");
        }

        SysUser user = userService.getByPhone(dto.getPhone());
        if (user == null) {
            // 静默注册
            user = createUserByPhone(dto.getPhone());
        }

        return user;
    }

    /**
     * 微信小程序登录
     */
    @Transactional(rollbackFor = Exception.class)
    protected SysUser loginByWechat(LoginDTO dto) {
        if (StringUtils.isBlank(dto.getCode())) {
            throw new BusinessException("微信授权码不能为空");
        }

        // 调用微信接口获取 openid
        AuthInfoResult authInfo = wechatService.login(dto.getCode());
        String openid = authInfo.getOpenid();
        String unionId = authInfo.getUnionId();

        log.info("微信小程序登录成功，openid: {}, unionId: {}", openid, unionId);

        String socialTypeCode = SocialType.WECHAT_MINIAPP.getCode();
        SysUserSocial social = userSocialService.getBySocialTypeAndId(socialTypeCode, openid);
        if (social != null) {
            // 已绑定用户，直接返回
            SysUserDetailVO userVO = userService.getById(social.getUserId());
            if (userVO != null) {
                return MapStructUtils.convert(userVO, SysUser.class);
            }
            // 用户不存在（异常情况），创建新用户
            return createUserBySocial(socialTypeCode, openid, unionId);
        } else {
            // 创建新用户并绑定
            return createUserBySocial(socialTypeCode, openid, unionId);
        }
    }

    /**
     * 第三方登录
     */
    @Transactional(rollbackFor = Exception.class)
    protected SysUser loginByThird(LoginDTO dto) {
        if (StringUtils.isBlank(dto.getCode())) {
            throw new BusinessException("授权码不能为空");
        }
        if (StringUtils.isBlank(dto.getSocialType())) {
            throw new BusinessException("第三方平台类型不能为空");
        }

        // TODO: 根据 socialType 调用不同的第三方接口获取用户信息
        String socialId = "mock_social_id_" + dto.getCode();

        SysUserSocial social = userSocialService.getBySocialTypeAndId(dto.getSocialType(), socialId);
        if (social != null) {
            return createUserEntityFromId(social.getUserId());
        } else {
            return createUserBySocial(dto.getSocialType(), socialId, null);
        }
    }

    /**
     * 飞书登录
     */
    @Transactional(rollbackFor = Exception.class)
    protected SysUser loginByFeishu(LoginDTO dto) {
        if (StringUtils.isBlank(dto.getCode())) {
            throw new BusinessException("飞书授权码不能为空");
        }

        // 通过授权码获取飞书用户信息
        FeishuUserDTO feishuUser = feishuService.getUserInfo(dto.getCode());
        if (feishuUser == null || StringUtils.isBlank(feishuUser.getOpenId())) {
            throw new BusinessException("获取飞书用户信息失败");
        }

        String feishuSocialType = SocialType.FEISHU.getCode();
        String socialId = feishuUser.getOpenId();

        // 查找是否已绑定用户
        SysUserSocial social = userSocialService.getBySocialTypeAndId(feishuSocialType, socialId);
        if (social != null) {
            // 已绑定用户，直接返回
            SysUserDetailVO userVO = userService.getById(social.getUserId());
            if (userVO == null) {
                throw new BusinessException("绑定的用户不存在，请联系管理员");
            }
            // 转换为 SysUser 实体
            SysUser user = MapStructUtils.convert(userVO, SysUser.class);
            return user;
        }

        // 未绑定用户，创建新用户并绑定
        return createUserByFeishu(feishuUser);
    }

    /** 默认用户角色ID（普通用户） */
    private static final Long DEFAULT_USER_ROLE_ID = 3L;

    /**
     * 通过飞书信息创建用户
     */
    private SysUser createUserByFeishu(FeishuUserDTO feishuUser) {
        // 创建用户 DTO
        SysUserCreateDTO createDTO = new SysUserCreateDTO();
        createDTO.setNickname(StringUtils.isNotBlank(feishuUser.getName()) ? feishuUser.getName() : "飞书用户");
        createDTO.setRealName(feishuUser.getName());
        createDTO.setAvatar(feishuUser.getAvatarUrl());
        createDTO.setEmail(feishuUser.getEmail());
        createDTO.setPhone(feishuUser.getMobile());
        createDTO.setStatus(1);
        // 生成唯一用户名
        createDTO.setUsername("feishu_" + feishuUser.getOpenId().substring(0, 8));
        // 设置随机密码（飞书用户一般不使用密码登录）
        createDTO.setPassword(UUID.randomUUID().toString());
        // 分配默认角色（普通用户）
        createDTO.setRoleIds(List.of(DEFAULT_USER_ROLE_ID));

        // 保存用户
        Long userId = userService.create(createDTO);

        // 绑定社交账号
        SysUserSocial social = new SysUserSocial();
        social.setUserId(userId);
        social.setSocialType(SocialType.FEISHU.getCode());
        social.setSocialId(feishuUser.getOpenId());
        social.setUnionId(feishuUser.getUnionId());
        social.setNickname(feishuUser.getName());
        social.setAvatar(feishuUser.getAvatarUrl());
        social.setBindTime(LocalDateTime.now());
        userSocialService.saveOrUpdate(social);

        log.info("飞书登录创建新用户, userId={}, openId={}, name={}",
                userId, feishuUser.getOpenId(), feishuUser.getName());

        // 返回用户实体
        SysUserDetailVO userVO = userService.getById(userId);
        return MapStructUtils.convert(userVO, SysUser.class);
    }

    /**
     * 测试模式登录
     */
    private SysUser loginByTest(LoginDTO dto) {
        // 测试模式：直接使用用户名登录
        if (StringUtils.isBlank(dto.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        SysUser user = userService.getByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 通过手机号创建用户
     */
    private SysUser createUserByPhone(String phone) {
        // 生成唯一用户名（phone_ + 手机号后4位 + 随机4位）
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4);
        String username = "phone_" + phone.substring(phone.length() - 4) + "_" + randomSuffix;
        String nickname = "用户" + phone.substring(phone.length() - 4);

        // 创建用户 DTO
        SysUserCreateDTO createDTO = new SysUserCreateDTO();
        createDTO.setUsername(username);
        createDTO.setPassword(UUID.randomUUID().toString().substring(0, 16)); // 随机密码
        createDTO.setPasswordSet(0); // 标记为未设置密码
        createDTO.setPhone(phone);
        createDTO.setNickname(nickname);
        createDTO.setStatus(1);
        createDTO.setRoleIds(List.of(DEFAULT_USER_ROLE_ID)); // 分配默认角色

        // 创建用户
        Long userId = userService.create(createDTO);
        log.info("手机号静默注册用户成功, userId={}, phone={}", userId, maskPhone(phone));

        // 返回完整用户实体
        return userService.getByUsername(username);
    }

    /**
     * 通过第三方创建用户
     *
     * @param socialType 社交平台类型
     * @param socialId   社交平台用户ID（如 openid）
     * @param unionId    联合ID（可选）
     * @return 创建的用户
     */
    private SysUser createUserBySocial(String socialType, String socialId, String unionId) {
        // 生成唯一用户名（wx_ + openid前8位 + 随机4位）
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4);
        String username = "wx_" + socialId.substring(0, Math.min(8, socialId.length())) + "_" + randomSuffix;
        String nickname = "微信用户" + socialId.substring(socialId.length() - 4);

        // 创建用户 DTO
        SysUserCreateDTO createDTO = new SysUserCreateDTO();
        createDTO.setUsername(username);
        createDTO.setPassword(UUID.randomUUID().toString().substring(0, 16)); // 随机密码，用户可后续修改
        createDTO.setPasswordSet(0);
        createDTO.setNickname(nickname);
        createDTO.setStatus(1);

        // 创建用户
        Long userId = userService.create(createDTO);
        log.info("微信静默注册用户成功, userId={}, username={}, socialType={}, socialId={}",
                userId, username, socialType, socialId);

        // 绑定社交账号
        SysUserSocial social = new SysUserSocial();
        social.setUserId(userId);
        social.setSocialType(socialType);
        social.setSocialId(socialId);
        social.setUnionId(unionId);
        social.setBindTime(LocalDateTime.now());
        userSocialService.saveOrUpdate(social);
        log.info("绑定社交账号成功, userId={}, socialType={}, socialId={}", userId, socialType, socialId);

        // 返回完整用户实体
        return userService.getByUsername(username);
    }

    /**
     * 从ID构建用户实体（简化处理）
     */
    private SysUser createUserEntityFromId(Long userId) {
        // 实际应该从数据库查询
        SysUser user = new SysUser();
        user.setId(userId);
        return user;
    }

    /**
     * 构建登录用户
     */
    private LoginUser buildLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();

        // 基础信息
        BaseUserInfo userInfo = new BaseUserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setName(user.getRealName() != null ? user.getRealName() : user.getNickname());
        userInfo.setNickname(user.getNickname());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setEmail(user.getEmail());
        userInfo.setDeptId(user.getDeptId());

        // 从用户获取数据权限范围
        DataScopeEnum dataScope = DataScopeEnum.matchCode(user.getDataScope());
        userInfo.setDataScope(dataScope);
        loginUser.setUserInfo(userInfo);

        // 如果是本部门及下级部门权限，加载子部门ID列表
        if (DataScopeEnum.DeptAndChildren.equals(dataScope) && user.getDeptId() != null) {
            List<Long> childDeptIds = deptService.getChildDeptIds(user.getDeptId());
            loginUser.setDeptAndChildren(childDeptIds);
        }

        // 角色
        Set<String> roleCodes = userService.getUserRoleCodes(user.getId());
        loginUser.setRoleKeys(roleCodes);

        // 权限
        Set<String> permissions = userService.getUserPermissions(user.getId());
        loginUser.setPermissionKeys(permissions);

        return loginUser;
    }

    /**
     * 构建登录响应
     */
    private LoginVO buildLoginVO(SysUser user, LoginUser loginUser) {
        LoginVO vo = new LoginVO();
        vo.setAccessToken(StpUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName() != null ? user.getRealName() : user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(new ArrayList<>(loginUser.getRoleKeys()));
        vo.setHomePath(StringUtils.isNotBlank(user.getHomePath()) ? user.getHomePath() : "/dashboard");
        // 手机号脱敏后返回，用于前端判断是否需要收集手机号
        vo.setPhone(maskPhone(user.getPhone()));
        // 判断用户信息是否完整（有头像且有昵称）
        vo.setProfileComplete(StringUtils.isNotBlank(user.getAvatar()) && StringUtils.isNotBlank(user.getNickname()));
        return vo;
    }

}
