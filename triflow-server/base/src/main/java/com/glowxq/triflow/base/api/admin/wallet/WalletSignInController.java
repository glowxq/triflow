package com.glowxq.triflow.base.api.admin.wallet;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletSignInConfigUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletSignInQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInConfigVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInVO;
import com.glowxq.triflow.base.wallet.service.WalletSignInService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 钱包签到管理控制器（管理后台）
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Tag(name = "钱包签到", description = "钱包签到配置与记录")
@AdminApi
@RestController
@RequestMapping("/base/admin/wallet/signin")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Wallet)
public class WalletSignInController extends BaseApi {

    private final WalletSignInService signInService;

    @Operation(summary = "签到配置")
    @SaCheckPermission("wallet:signin:query")
    @GetMapping("/config")
    public ApiResult<WalletSignInConfigVO> config() {
        return ApiResult.success(signInService.getConfig());
    }

    @Operation(summary = "更新签到配置")
    @SaCheckPermission("wallet:signin:update")
    @PutMapping("/config")
    public ApiResult<Boolean> updateConfig(@Valid @RequestBody WalletSignInConfigUpdateDTO dto) {
        boolean success = signInService.updateConfig(dto);
        return ApiResult.success(success);
    }

    @Operation(summary = "签到记录分页")
    @SaCheckPermission("wallet:signin:query")
    @GetMapping("/page")
    public ApiResult<Page<WalletSignInVO>> page(WalletSignInQuery query) {
        return ApiResult.success(signInService.page(query));
    }
}
