package com.glowxq.triflow.base.wallet.service;

import com.glowxq.triflow.base.wallet.entity.WalletRechargeConfig;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeConfigCreateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeConfigUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeConfigQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeConfigVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 钱包充值配置服务接口
 *
 * @author glowxq
 * @since 2025-02-10
 */
public interface WalletRechargeConfigService {

    Long create(WalletRechargeConfigCreateDTO dto);

    boolean update(WalletRechargeConfigUpdateDTO dto);

    WalletRechargeConfigVO getById(Long id);

    boolean deleteById(Long id);

    boolean deleteByIds(List<Long> ids);

    Page<WalletRechargeConfigVO> page(WalletRechargeConfigQuery query);

    List<WalletRechargeConfigVO> listEnabled(String type);

    WalletRechargeConfig getEntityById(Long id);
}
