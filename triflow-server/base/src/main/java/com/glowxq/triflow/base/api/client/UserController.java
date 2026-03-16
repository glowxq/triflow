package com.glowxq.triflow.base.api.client;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.auth.pojo.vo.UserInfoVO;
import com.glowxq.triflow.base.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.ClientApi;

/**
 * 用户信息控制器
 * <p>
 * 提供当前用户信息相关接口，独立于 /auth 路径。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Tag(name = "用户信息", description = "获取当前登录用户信息")
@ClientApi
@RestController
@RequestMapping("/base/client/user")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Auth)
public class UserController extends BaseApi {

    private final AuthService authService;

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public ApiResult<UserInfoVO> getUserInfo() {
        UserInfoVO userInfo = authService.getUserInfo();
        return ApiResult.success(userInfo);
    }

}
