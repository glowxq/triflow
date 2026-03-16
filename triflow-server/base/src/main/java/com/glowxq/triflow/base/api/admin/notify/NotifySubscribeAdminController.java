package com.glowxq.triflow.base.api.admin.notify;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeQueryDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeUpdateDTO;
import com.glowxq.triflow.base.notify.pojo.vo.NotifySubscribeVO;
import com.glowxq.triflow.base.notify.service.NotifySubscribeService;
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
 * 消息订阅管理控制器（后台）
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Tag(name = "消息订阅管理", description = "后台管理用户订阅记录")
@AdminApi
@RestController
@RequestMapping("/base/admin/notify/subscribe")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Notify)
public class NotifySubscribeAdminController extends BaseApi {

    private final NotifySubscribeService subscribeService;

    @Operation(summary = "订阅列表")
    @SaCheckPermission("wechat:subscribe:query")
    @PostMapping("/list")
    public ApiResult<List<NotifySubscribeVO>> list(@RequestBody NotifySubscribeQueryDTO query) {
        List<NotifySubscribeVO> list = subscribeService.list(query);
        return ApiResult.success(list);
    }

    @Operation(summary = "订阅分页")
    @SaCheckPermission("wechat:subscribe:query")
    @PostMapping("/page")
    public ApiResult<Page<NotifySubscribeVO>> page(@RequestBody NotifySubscribeQueryDTO query) {
        Page<NotifySubscribeVO> page = subscribeService.page(query);
        return ApiResult.success(page);
    }

    @Operation(summary = "更新订阅状态")
    @SaCheckPermission("wechat:subscribe:update")
    @PutMapping("/updateStatus")
    public ApiResult<Boolean> updateStatus(@Valid @RequestBody NotifySubscribeUpdateDTO dto) {
        boolean success = subscribeService.updateStatus(dto);
        return ApiResult.success(success);
    }

    @Operation(summary = "删除订阅记录")
    @SaCheckPermission("wechat:subscribe:delete")
    @DeleteMapping("/delete")
    public ApiResult<Boolean> delete(@Parameter(description = "订阅ID") @RequestParam Long id) {
        boolean success = subscribeService.deleteById(id);
        return ApiResult.success(success);
    }

    @Operation(summary = "批量删除订阅记录")
    @SaCheckPermission("wechat:subscribe:deleteBatch")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = subscribeService.deleteByIds(ids);
        return ApiResult.success(success);
    }
}
