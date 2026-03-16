package com.glowxq.triflow.base.wallet.pojo.query;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 钱包充值配置查询参数
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "钱包充值配置查询参数")
public class WalletRechargeConfigQuery extends PageQuery {

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "充值类型")
    private String type;

    @Schema(description = "状态")
    private Integer status;
}
