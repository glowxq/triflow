package com.glowxq.triflow.base.wallet.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包充值配置 VO
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@Schema(description = "钱包充值配置")
@AutoMapper(target = WalletRechargeConfig.class)
public class WalletRechargeConfigVO implements BaseVO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "充值类型")
    private String type;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "到账金额")
    private BigDecimal rewardAmount;

    @Schema(description = "赠送金额")
    private BigDecimal bonusAmount;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
