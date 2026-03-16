package com.glowxq.triflow.base.api.admin.wechat;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarCreateDTO;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarQueryDTO;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarUpdateDTO;
import com.glowxq.triflow.base.wechat.pojo.vo.WechatTabbarVO;
import com.glowxq.triflow.base.wechat.service.WechatTabbarService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 微信小程序底部菜单管理
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Tag(name = "微信底部菜单", description = "微信小程序底部菜单管理接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/wechat/tabbar")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Wechat)
public class WechatTabbarController extends BaseApi {

    private final WechatTabbarService tabbarService;

    @Operation(summary = "创建底部菜单")
    @SaCheckPermission("wechat:tabbar:create")
    @PostMapping("/create")
    public ApiResult<Long> create(@Valid @RequestBody WechatTabbarCreateDTO dto) {
        Long id = tabbarService.create(dto);
        return ApiResult.success(id);
    }

    @Operation(summary = "更新底部菜单")
    @SaCheckPermission("wechat:tabbar:update")
    @PutMapping("/update")
    public ApiResult<Boolean> update(@Valid @RequestBody WechatTabbarUpdateDTO dto) {
        boolean success = tabbarService.update(dto);
        return ApiResult.success(success);
    }

    @Operation(summary = "底部菜单详情")
    @SaCheckPermission("wechat:tabbar:query")
    @GetMapping("/detail")
    public ApiResult<WechatTabbarVO> detail(@Parameter(description = "菜单ID") @RequestParam Long id) {
        WechatTabbarVO detail = tabbarService.getById(id);
        return ApiResult.success(detail);
    }

    @Operation(summary = "删除底部菜单")
    @SaCheckPermission("wechat:tabbar:delete")
    @DeleteMapping("/delete")
    public ApiResult<Boolean> delete(@Parameter(description = "菜单ID") @RequestParam Long id) {
        boolean success = tabbarService.deleteById(id);
        return ApiResult.success(success);
    }

    @Operation(summary = "批量删除底部菜单")
    @SaCheckPermission("wechat:tabbar:deleteBatch")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = tabbarService.deleteByIds(ids);
        return ApiResult.success(success);
    }

    @Operation(summary = "底部菜单列表")
    @SaCheckPermission("wechat:tabbar:query")
    @PostMapping("/list")
    public ApiResult<List<WechatTabbarVO>> list(@RequestBody WechatTabbarQueryDTO query) {
        List<WechatTabbarVO> list = tabbarService.list(query);
        return ApiResult.success(list);
    }

    @Operation(summary = "底部菜单分页")
    @SaCheckPermission("wechat:tabbar:query")
    @PostMapping("/page")
    public ApiResult<Page<WechatTabbarVO>> page(@RequestBody WechatTabbarQueryDTO query) {
        Page<WechatTabbarVO> page = tabbarService.page(query);
        return ApiResult.success(page);
    }
}
