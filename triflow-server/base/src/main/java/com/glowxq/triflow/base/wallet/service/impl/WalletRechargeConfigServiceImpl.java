package com.glowxq.triflow.base.wallet.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeConfig;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import com.glowxq.triflow.base.wallet.mapper.WalletRechargeConfigMapper;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeConfigCreateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeConfigUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeConfigQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeConfigVO;
import com.glowxq.triflow.base.wallet.service.WalletRechargeConfigService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包充值配置服务实现
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Service
@RequiredArgsConstructor
public class WalletRechargeConfigServiceImpl implements WalletRechargeConfigService {

    private final WalletRechargeConfigMapper configMapper;

    @Override
    public Long create(WalletRechargeConfigCreateDTO dto) {
        validateConfig(dto.getType(), dto.getRewardAmount());
        WalletRechargeConfig entity = MapStructUtils.convert(dto, WalletRechargeConfig.class);
        if (entity.getBonusAmount() == null) {
            entity.setBonusAmount(BigDecimal.ZERO);
        }
        configMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public boolean update(WalletRechargeConfigUpdateDTO dto) {
        WalletRechargeConfig existing = configMapper.selectOneById(dto.getId());
        if (existing == null) {
            throw new BusinessException("充值配置不存在");
        }
        validateConfig(dto.getType(), dto.getRewardAmount());
        WalletRechargeConfig entity = MapStructUtils.convert(dto, WalletRechargeConfig.class);
        if (entity.getBonusAmount() == null) {
            entity.setBonusAmount(BigDecimal.ZERO);
        }
        return configMapper.update(entity) > 0;
    }

    @Override
    public WalletRechargeConfigVO getById(Long id) {
        WalletRechargeConfig entity = configMapper.selectOneById(id);
        if (entity == null) {
            throw new BusinessException("充值配置不存在");
        }
        return MapStructUtils.convert(entity, WalletRechargeConfigVO.class);
    }

    @Override
    public boolean deleteById(Long id) {
        return configMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return configMapper.deleteBatchByIds(ids) > 0;
    }

    @Override
    public Page<WalletRechargeConfigVO> page(WalletRechargeConfigQuery query) {
        QueryWrapper qw = configMapper.buildQueryWrapper(query);
        Page<WalletRechargeConfigVO> page = query.buildPage();
        return configMapper.paginateAs(page, qw, WalletRechargeConfigVO.class);
    }

    @Override
    public List<WalletRechargeConfigVO> listEnabled(String type) {
        List<WalletRechargeConfig> list = configMapper.selectEnabledByType(type);
        return MapStructUtils.convert(list, WalletRechargeConfigVO.class);
    }

    @Override
    public WalletRechargeConfig getEntityById(Long id) {
        return configMapper.selectOneById(id);
    }

    private void validateConfig(String type, BigDecimal rewardAmount) {
        if (StringUtils.isBlank(type)) {
            throw new BusinessException("充值类型不能为空");
        }
        WalletType walletType = WalletType.fromCode(type);
        if (walletType == null) {
            throw new BusinessException("充值类型无效");
        }
        if (rewardAmount == null || rewardAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("到账金额必须大于0");
        }
        if (walletType == WalletType.POINTS && rewardAmount.stripTrailingZeros().scale() > 0) {
            throw new BusinessException("积分到账金额必须为整数");
        }
    }
}
