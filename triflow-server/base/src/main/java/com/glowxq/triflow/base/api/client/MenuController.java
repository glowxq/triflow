package com.glowxq.triflow.base.api.client;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuTreeVO;
import com.glowxq.triflow.base.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.ClientApi;

/**
 * 菜单控制器
 * <p>
 * 提供用户菜单相关接口，独立于 /auth 路径。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Tag(name = "菜单", description = "获取用户菜单")
@ClientApi
@RestController
@RequestMapping("/base/client/menu")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Auth)
public class MenuController extends BaseApi {

    private final SysMenuService menuService;

    /**
     * 获取用户菜单
     */
    @Operation(summary = "获取用户菜单", description = "获取当前用户的菜单树")
    @GetMapping("/all")
    public ApiResult<List<SysMenuTreeVO>> getUserMenus() {
        Long userId = LoginUtils.getUserId();
        List<SysMenuTreeVO> menus = menuService.getUserMenuTree(userId);
        return ApiResult.success(menus);
    }

}
