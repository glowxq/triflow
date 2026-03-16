package com.glowxq.triflow.base.api.client;

import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeSubmitDTO;
import com.glowxq.triflow.base.notify.pojo.vo.NotifySubscribeVO;
import com.glowxq.triflow.base.notify.service.NotifySubscribeService;
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
 * 消息订阅控制器
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Tag(name = "消息订阅", description = "消息订阅接口")
@ClientApi
@RestController
@RequestMapping("/base/client/notify/subscribe")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Notify)
public class NotifySubscribeController extends BaseApi {

    private final NotifySubscribeService subscribeService;

    @Operation(summary = "提交订阅结果")
    @PostMapping("/submit")
    public ApiResult<Void> submit(@Valid @RequestBody NotifySubscribeSubmitDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        subscribeService.submit(userId, dto);
        return ApiResult.success();
    }

    @Operation(summary = "查询订阅列表")
    @GetMapping("/list")
    public ApiResult<List<NotifySubscribeVO>> list(
            @Parameter(description = "通知渠道") @RequestParam String channel) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<NotifySubscribeVO> list = subscribeService.listByUser(userId, channel);
        return ApiResult.success(list);
    }
}
