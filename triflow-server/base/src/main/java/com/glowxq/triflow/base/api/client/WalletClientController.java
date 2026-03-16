package com.glowxq.triflow.base.api.client;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeCreateDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeOrderQuery;
import com.glowxq.triflow.base.wallet.pojo.query.WalletTransactionQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeConfigVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeOrderVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargePayVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInStatusVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletTransactionVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInVO;
import com.glowxq.triflow.base.wallet.service.WalletRechargeConfigService;
import com.glowxq.triflow.base.wallet.service.WalletRechargeOrderService;
import com.glowxq.triflow.base.wallet.service.WalletSignInService;
import com.glowxq.triflow.base.wallet.service.WalletService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.ClientApi;

/**
 * 钱包客户端控制器（C端用户）
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Tag(name = "我的钱包", description = "用户钱包查询接口")
@ClientApi
@RestController
@RequestMapping("/base/client/wallet")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Wallet)
public class WalletClientController extends BaseApi {

    private final WalletService walletService;
    private final WalletSignInService signInService;
    private final WalletRechargeConfigService rechargeConfigService;
    private final WalletRechargeOrderService rechargeOrderService;

    /**
     * 查询我的钱包变动记录
     */
    @Operation(summary = "查询我的变动记录", description = "分页查询当前用户的积分余额变动记录")
    @GetMapping("/transactions")
    public ApiResult<Page<WalletTransactionVO>> myTransactions(WalletTransactionQuery query) {
        return ApiResult.success(walletService.myTransactions(query));
    }

    @Operation(summary = "签到状态", description = "获取当前用户的签到状态")
    @GetMapping("/signin/status")
    public ApiResult<WalletSignInStatusVO> signInStatus() {
        return ApiResult.success(signInService.getStatus());
    }

    @Operation(summary = "立即签到", description = "用户每日签到")
    @PostMapping("/signin")
    public ApiResult<WalletSignInVO> signIn() {
        return ApiResult.success(signInService.signIn());
    }

    @Operation(summary = "充值配置列表", description = "获取可用的充值配置列表")
    @GetMapping("/recharge/configs")
    public ApiResult<List<WalletRechargeConfigVO>> rechargeConfigs(@RequestParam(required = false) String type) {
        return ApiResult.success(rechargeConfigService.listEnabled(type));
    }

    @Operation(summary = "充值下单", description = "创建充值订单并返回微信支付参数")
    @PostMapping("/recharge")
    public ApiResult<WalletRechargePayVO> recharge(@Valid @RequestBody WalletRechargeCreateDTO dto) {
        return ApiResult.success(rechargeOrderService.createOrder(dto));
    }

    @Operation(summary = "充值订单列表", description = "分页查询当前用户充值订单")
    @GetMapping("/recharge/orders")
    public ApiResult<Page<WalletRechargeOrderVO>> rechargeOrders(WalletRechargeOrderQuery query) {
        return ApiResult.success(rechargeOrderService.myOrders(query));
    }
}
