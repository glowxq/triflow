package com.glowxq.triflow.base.api.admin.wallet;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeConfigCreateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeConfigUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeConfigQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeConfigVO;
import com.glowxq.triflow.base.wallet.service.WalletRechargeConfigService;
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
 * 钱包充值配置控制器（管理后台）
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Tag(name = "钱包充值配置", description = "钱包充值配置管理")
@AdminApi
@RestController
@RequestMapping("/base/admin/wallet/recharge-config")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Wallet)
public class WalletRechargeConfigController extends BaseApi {

    private final WalletRechargeConfigService configService;

    @Operation(summary = "创建充值配置")
    @SaCheckPermission("wallet:recharge:create")
    @PostMapping("/create")
    public ApiResult<Long> create(@Valid @RequestBody WalletRechargeConfigCreateDTO dto) {
        Long id = configService.create(dto);
        return ApiResult.success(id);
    }

    @Operation(summary = "更新充值配置")
    @SaCheckPermission("wallet:recharge:update")
    @PutMapping("/update")
    public ApiResult<Boolean> update(@Valid @RequestBody WalletRechargeConfigUpdateDTO dto) {
        boolean success = configService.update(dto);
        return ApiResult.success(success);
    }

    @Operation(summary = "充值配置详情")
    @SaCheckPermission("wallet:recharge:query")
    @GetMapping("/detail")
    public ApiResult<WalletRechargeConfigVO> detail(@Parameter(description = "配置ID") @RequestParam Long id) {
        WalletRechargeConfigVO detail = configService.getById(id);
        return ApiResult.success(detail);
    }

    @Operation(summary = "删除充值配置")
    @SaCheckPermission("wallet:recharge:delete")
    @DeleteMapping("/delete")
    public ApiResult<Boolean> delete(@Parameter(description = "配置ID") @RequestParam Long id) {
        boolean success = configService.deleteById(id);
        return ApiResult.success(success);
    }

    @Operation(summary = "批量删除充值配置")
    @SaCheckPermission("wallet:recharge:delete")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean success = configService.deleteByIds(ids);
        return ApiResult.success(success);
    }

    @Operation(summary = "充值配置分页")
    @SaCheckPermission("wallet:recharge:query")
    @PostMapping("/page")
    public ApiResult<Page<WalletRechargeConfigVO>> page(@RequestBody WalletRechargeConfigQuery query) {
        Page<WalletRechargeConfigVO> page = configService.page(query);
        return ApiResult.success(page);
    }
}
