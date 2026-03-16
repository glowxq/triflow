package com.glowxq.triflow.base.wallet.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 钱包充值配置更新 DTO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包充值配置更新")
@AutoMapper(target = WalletRechargeConfig.class)
public class WalletRechargeConfigUpdateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "配置ID不能为空")
    @Schema(description = "配置ID")
    private Long id;

    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过100字符")
    @Schema(description = "配置名称")
    private String configName;

    @NotBlank(message = "充值类型不能为空")
    @Schema(description = "充值类型")
    private String type;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @NotNull(message = "到账金额不能为空")
    @DecimalMin(value = "0.01", message = "到账金额必须大于0")
    @Schema(description = "到账金额")
    private BigDecimal rewardAmount;

    @Schema(description = "赠送金额")
    private BigDecimal bonusAmount;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Size(max = 500, message = "备注长度不能超过500字符")
    @Schema(description = "备注")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, WalletRechargeConfig.class);
    }
}
