package com.glowxq.triflow.base.wallet.mapper;

import com.glowxq.triflow.base.wallet.entity.WalletRechargeConfig;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeConfigQuery;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 钱包充值配置 Mapper
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Mapper
public interface WalletRechargeConfigMapper extends BaseMapper<WalletRechargeConfig> {

    default Page<WalletRechargeConfig> paginateByQuery(int pageNum, int pageSize, WalletRechargeConfigQuery query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    default List<WalletRechargeConfig> selectByQuery(WalletRechargeConfigQuery query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    default List<WalletRechargeConfig> selectEnabledByType(String type) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .from(WalletRechargeConfig.class)
                                           .eq(WalletRechargeConfig::getStatus, 1)
                                           .eq(WalletRechargeConfig::getType, type, StringUtils.isNotBlank(type))
                                           .orderBy(WalletRechargeConfig::getSort, true)
                                           .orderBy(WalletRechargeConfig::getId, true);
        return selectListByQuery(wrapper);
    }

    default QueryWrapper buildQueryWrapper(WalletRechargeConfigQuery query) {
        return QueryWrapper.create()
                           .from(WalletRechargeConfig.class)
                           .like(WalletRechargeConfig::getConfigName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(WalletRechargeConfig::getType, query.getType(), StringUtils.isNotBlank(query.getType()))
                           .eq(WalletRechargeConfig::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(WalletRechargeConfig::getSort, true)
                           .orderBy(WalletRechargeConfig::getId, true);
    }
}
