package com.glowxq.triflow.base.api.admin.wallet;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletAdjustDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletTransactionQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletTransactionVO;
import com.glowxq.triflow.base.wallet.service.WalletService;
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
 * 钱包管理控制器（管理后台）
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Tag(name = "钱包管理", description = "积分余额变动管理（管理后台）")
@AdminApi
@RestController
@RequestMapping("/base/admin/wallet/transaction")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Wallet)
public class WalletController extends BaseApi {

    private final WalletService walletService;

    /**
     * 分页查询变动记录
     */
    @Operation(summary = "分页查询变动记录", description = "查询所有用户的钱包变动记录")
    @SaCheckPermission("wallet:transaction:query")
    @GetMapping("/page")
    public ApiResult<Page<WalletTransactionVO>> page(WalletTransactionQuery query) {
        return ApiResult.success(walletService.page(query));
    }

    /**
     * 调整用户钱包
     */
    @Operation(summary = "调整用户钱包", description = "管理员手动调整用户积分/余额")
    @SaCheckPermission("wallet:transaction:adjust")
    @PostMapping("/adjust")
    public ApiResult<Void> adjust(@Valid @RequestBody WalletAdjustDTO dto) {
        walletService.adjust(dto);
        return ApiResult.success(null);
    }
}
