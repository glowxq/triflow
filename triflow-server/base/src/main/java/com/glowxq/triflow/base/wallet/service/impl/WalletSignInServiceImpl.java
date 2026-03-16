package com.glowxq.triflow.base.wallet.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.system.entity.SysConfig;
import com.glowxq.triflow.base.system.entity.SysSwitch;
import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.enums.ConfigKeyEnum;
import com.glowxq.triflow.base.system.enums.SwitchKeyEnum;
import com.glowxq.triflow.base.system.mapper.SysUserMapper;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigUpdateDTO;
import com.glowxq.triflow.base.system.service.SysConfigService;
import com.glowxq.triflow.base.system.service.SysSwitchService;
import com.glowxq.triflow.base.wallet.entity.WalletSignIn;
import com.glowxq.triflow.base.wallet.mapper.WalletSignInMapper;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletSignInConfigUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletSignInQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInConfigVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInStatusVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInVO;
import com.glowxq.triflow.base.wallet.service.WalletService;
import com.glowxq.triflow.base.wallet.service.WalletSignInService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 钱包签到服务实现
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletSignInServiceImpl implements WalletSignInService {

    private static final long DEFAULT_SIGNIN_POINTS = 10L;

    private final WalletSignInMapper signInMapper;
    private final SysUserMapper userMapper;
    private final WalletService walletService;
    private final SysConfigService configService;
    private final SysSwitchService switchService;

    @Override
    public WalletSignInStatusVO getStatus() {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        LocalDate today = LocalDate.now();
        WalletSignIn todayRecord = signInMapper.selectByUserIdAndDate(userId, today);
        WalletSignIn latestRecord = signInMapper.selectLatestByUserId(userId);

        WalletSignInStatusVO status = new WalletSignInStatusVO();
        status.setSignedToday(todayRecord != null);
        status.setConsecutiveDays(latestRecord != null ? latestRecord.getConsecutiveDays() : 0);
        status.setRewardPoints(getSignInPoints());
        status.setLastSignDate(latestRecord != null ? latestRecord.getSignDate() : null);
        return status;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletSignInVO signIn() {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        if (!isSignInEnabled()) {
            throw new BusinessException("签到功能已关闭");
        }

        LocalDate today = LocalDate.now();
        WalletSignIn todayRecord = signInMapper.selectByUserIdAndDate(userId, today);
        if (todayRecord != null) {
            throw new BusinessException("今天已签到");
        }

        WalletSignIn latestRecord = signInMapper.selectLatestByUserId(userId);
        int consecutiveDays = 1;
        if (latestRecord != null && today.minusDays(1).equals(latestRecord.getSignDate())) {
            consecutiveDays = latestRecord.getConsecutiveDays() + 1;
        }

        long points = getSignInPoints();
        WalletSignIn record = new WalletSignIn();
        record.setUserId(userId);
        record.setSignDate(today);
        record.setPoints(points);
        record.setConsecutiveDays(consecutiveDays);
        signInMapper.insert(record);

        walletService.addPoints(userId, points, "每日签到", "signin", String.valueOf(record.getId()), "每日签到奖励");

        WalletSignInVO vo = MapStructUtils.convert(record, WalletSignInVO.class);
        SysUser user = userMapper.selectOneById(userId);
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
        }
        return vo;
    }

    @Override
    public Page<WalletSignInVO> page(WalletSignInQuery query) {
        Page<WalletSignIn> page;
        if (StringUtils.isNotBlank(query.getUsername())) {
            List<SysUser> matchedUsers = userMapper.selectByKeyword(query.getUsername());
            List<Long> userIds = matchedUsers.stream().map(SysUser::getId).toList();
            page = signInMapper.paginateByQueryAndUserIds(
                query.getPageNum(), query.getPageSize(), query, userIds);
        } else {
            QueryWrapper qw = signInMapper.buildQueryWrapper(query);
            page = signInMapper.paginate(query.buildPage(), qw);
        }

        List<WalletSignInVO> records = enrichRecords(page.getRecords());
        return new Page<>(records, page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    @Override
    public WalletSignInConfigVO getConfig() {
        WalletSignInConfigVO config = new WalletSignInConfigVO();
        config.setEnabled(isSignInEnabled());
        config.setPoints(getSignInPoints());
        return config;
    }

    @Override
    public boolean updateConfig(WalletSignInConfigUpdateDTO dto) {
        updateSwitchValue(SwitchKeyEnum.WALLET_SIGNIN_ENABLED, dto.getEnabled());
        updateConfigValue(ConfigKeyEnum.WALLET_SIGNIN_POINTS.getKey(), String.valueOf(dto.getPoints()));
        return true;
    }

    private List<WalletSignInVO> enrichRecords(List<WalletSignIn> records) {
        if (CollectionUtils.isEmpty(records)) {
            return List.of();
        }
        List<Long> userIds = records.stream().map(WalletSignIn::getUserId).distinct().toList();
        Map<Long, SysUser> userMap = userMapper.selectListByIds(userIds)
                                               .stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        return records.stream().map(record -> {
            WalletSignInVO vo = MapStructUtils.convert(record, WalletSignInVO.class);
            SysUser user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
            }
            return vo;
        }).toList();
    }

    private boolean isSignInEnabled() {
        return switchService.isEnabled(SwitchKeyEnum.WALLET_SIGNIN_ENABLED);
    }

    private long getSignInPoints() {
        String value = configService.getValueByKey(
            ConfigKeyEnum.WALLET_SIGNIN_POINTS.getKey(),
            String.valueOf(DEFAULT_SIGNIN_POINTS)
        );
        try {
            long points = Long.parseLong(value);
            // 如果配置的积分小于等于0，返回默认值，避免 addPoints 时报错
            return points > 0 ? points : DEFAULT_SIGNIN_POINTS;
        } catch (NumberFormatException e) {
            return DEFAULT_SIGNIN_POINTS;
        }
    }

    private void updateConfigValue(String key, String value) {
        SysConfig config = configService.getByConfigKey(key);
        if (config == null) {
            throw new BusinessException("配置不存在: " + key);
        }
        SysConfigUpdateDTO updateDTO = new SysConfigUpdateDTO();
        updateDTO.setId(config.getId());
        updateDTO.setConfigValue(value);
        configService.update(updateDTO);
    }

    private void updateSwitchValue(SwitchKeyEnum keyEnum, boolean enabled) {
        SysSwitch switchEntity = switchService.getBySwitchKey(keyEnum.getKey());
        if (switchEntity == null) {
            throw new BusinessException("开关不存在: " + keyEnum.getKey());
        }
        switchService.toggle(
            switchEntity.getId(),
            enabled ? 1 : 0,
            "wallet sign-in config update"
        );
    }
}
