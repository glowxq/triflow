package com.glowxq.triflow.base.api.client;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.auth.pojo.dto.BindSocialDTO;
import com.glowxq.triflow.base.auth.pojo.dto.BindWechatPhoneDTO;
import com.glowxq.triflow.base.auth.pojo.dto.LoginDTO;
import com.glowxq.triflow.base.auth.pojo.dto.ProfileUpdateDTO;
import com.glowxq.triflow.base.auth.pojo.dto.RegisterDTO;
import com.glowxq.triflow.base.auth.pojo.dto.UpdatePasswordDTO;
import com.glowxq.triflow.base.auth.pojo.vo.LoginVO;
import com.glowxq.triflow.base.auth.pojo.vo.UserInfoVO;
import com.glowxq.triflow.base.auth.pojo.vo.UserSocialVO;
import com.glowxq.triflow.base.auth.service.AuthService;
import com.glowxq.triflow.base.auth.service.FeishuService;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuTreeVO;
import com.glowxq.triflow.base.system.service.SysMenuService;
import com.glowxq.common.security.core.util.LoginUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.ClientApi;

/**
 * 认证控制器
 * <p>
 * 提供登录、登出、刷新Token等认证相关接口。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Tag(name = "认证管理", description = "登录、登出、Token刷新等接口")
@ClientApi
@RestController
@RequestMapping("/base/client/auth")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Auth)
public class AuthController extends BaseApi {

    private final AuthService authService;
    private final SysMenuService menuService;
    private final FeishuService feishuService;

    /**
     * 登录
     */
    @Operation(summary = "登录", description = "支持多种登录方式：账号密码、手机号、微信等")
    @PostMapping("/login")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO result = authService.login(dto);
        return ApiResult.success(result);
    }

    /**
     * 登出
     */
    @Operation(summary = "登出", description = "退出登录")
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        authService.logout();
        return ApiResult.success(null);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "使用手机号+短信验证码注册")
    @PostMapping("/register")
    public ApiResult<LoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        LoginVO result = authService.register(dto);
        return ApiResult.success(result);
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token", description = "刷新访问令牌")
    @PostMapping("/refresh")
    public ApiResult<LoginVO> refresh() {
        LoginVO result = authService.refreshToken();
        return ApiResult.success(result);
    }

    /**
     * 获取权限码
     */
    @Operation(summary = "获取权限码", description = "获取当前用户的权限码列表")
    @GetMapping("/codes")
    public ApiResult<List<String>> getCodes() {
        List<String> codes = authService.getPermissionCodes();
        return ApiResult.success(codes);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/user/info")
    public ApiResult<UserInfoVO> getUserInfo() {
        UserInfoVO userInfo = authService.getUserInfo();
        return ApiResult.success(userInfo);
    }

    /**
     * 获取用户菜单
     */
    @Operation(summary = "获取用户菜单", description = "获取当前用户的菜单树")
    @GetMapping("/menu/all")
    public ApiResult<List<SysMenuTreeVO>> getUserMenus() {
        Long userId = LoginUtils.getUserId();
        List<SysMenuTreeVO> menus = menuService.getUserMenuTree(userId);
        return ApiResult.success(menus);
    }

    // ==================== 个人资料管理 ====================

    /**
     * 更新个人资料
     */
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人信息")
    @PutMapping("/profile")
    public ApiResult<Void> updateProfile(@RequestBody ProfileUpdateDTO dto) {
        authService.updateProfile(dto);
        return ApiResult.success(null);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "更新当前登录用户密码")
    @PutMapping("/password")
    public ApiResult<Void> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto) {
        authService.updatePassword(dto);
        return ApiResult.success(null);
    }

    // ==================== 第三方账号绑定 ====================

    /**
     * 获取当前用户的第三方绑定列表
     */
    @Operation(summary = "获取第三方绑定列表", description = "获取当前用户绑定的第三方账号列表")
    @GetMapping("/socials")
    public ApiResult<List<UserSocialVO>> getUserSocials() {
        List<UserSocialVO> socials = authService.getUserSocials();
        return ApiResult.success(socials);
    }

    /**
     * 获取飞书授权 URL（用于绑定）
     */
    @Operation(summary = "获取飞书授权URL", description = "获取飞书 OAuth 授权页面地址")
    @GetMapping("/feishu/authUrl")
    public ApiResult<String> getFeishuAuthUrl(
            @RequestParam(required = false) String redirectUri,
            @RequestParam(required = false) String state) {
        String authUrl = feishuService.getAuthUrl(redirectUri, state);
        return ApiResult.success(authUrl);
    }

    /**
     * 绑定飞书账号
     */
    @Operation(summary = "绑定飞书账号", description = "将飞书账号绑定到当前用户")
    @PostMapping("/bind/feishu")
    public ApiResult<Void> bindFeishu(@Valid @RequestBody BindSocialDTO dto) {
        authService.bindFeishu(dto);
        return ApiResult.success(null);
    }

    /**
     * 绑定微信手机号
     */
    @Operation(summary = "绑定微信手机号", description = "使用微信授权码绑定手机号到当前用户")
    @PostMapping("/bind/wechat-phone")
    public ApiResult<Void> bindWechatPhone(@Valid @RequestBody BindWechatPhoneDTO dto) {
        authService.bindWechatPhone(dto);
        return ApiResult.success(null);
    }

    /**
     * 解绑第三方账号
     */
    @Operation(summary = "解绑第三方账号", description = "解除当前用户与第三方平台的绑定")
    @DeleteMapping("/unbind/{socialType}")
    public ApiResult<Void> unbindSocial(
            @Parameter(description = "第三方平台类型", required = true)
            @PathVariable String socialType) {
        authService.unbindSocial(socialType);
        return ApiResult.success(null);
    }

}
