package com.glowxq.triflow.base.api.admin.wallet;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeOrderUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeRefundDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeOrderQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeOrderVO;
import com.glowxq.triflow.base.wallet.service.WalletRechargeOrderService;
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
 * 钱包充值订单管理（管理后台）
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Tag(name = "钱包充值订单", description = "充值订单管理")
@AdminApi
@RestController
@RequestMapping("/base/admin/wallet/recharge-order")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Wallet)
public class WalletRechargeOrderController extends BaseApi {

    private final WalletRechargeOrderService orderService;

    @Operation(summary = "充值订单分页")
    @SaCheckPermission("wallet:recharge-order:query")
    @GetMapping("/page")
    public ApiResult<Page<WalletRechargeOrderVO>> page(WalletRechargeOrderQuery query) {
        return ApiResult.success(orderService.page(query));
    }

    @Operation(summary = "充值订单退款")
    @SaCheckPermission("wallet:recharge-order:refund")
    @PostMapping("/refund")
    public ApiResult<Void> refund(@Valid @RequestBody WalletRechargeRefundDTO dto) {
        orderService.refund(dto);
        return ApiResult.success(null);
    }

    @Operation(summary = "更新充值订单")
    @SaCheckPermission("wallet:recharge-order:update")
    @PutMapping("/update")
    public ApiResult<Boolean> update(@Valid @RequestBody WalletRechargeOrderUpdateDTO dto) {
        boolean success = orderService.update(dto);
        return ApiResult.success(success);
    }
}
