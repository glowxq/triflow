package com.glowxq.triflow.base.wallet.pojo.dto;

import com.glowxq.triflow.base.wallet.enums.WalletAction;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钱包调整请求 DTO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Schema(description = "钱包调整请求")
public class WalletAdjustDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    /**
     * 交易类型
     *
     * @see WalletType
     */
    @NotBlank(message = "交易类型不能为空")
    @Schema(description = "交易类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    /**
     * 操作类型
     *
     * @see WalletAction
     */
    @NotBlank(message = "操作类型不能为空")
    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String action;

    /**
     * 变动金额
     */
    @NotNull(message = "变动金额不能为空")
    @Schema(description = "变动金额（正数）", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private String bizId;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}
