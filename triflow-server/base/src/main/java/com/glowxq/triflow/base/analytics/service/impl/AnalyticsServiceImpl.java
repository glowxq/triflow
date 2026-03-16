package com.glowxq.triflow.base.analytics.service.impl;

import com.glowxq.triflow.base.ai.enums.AiCallStatusEnum;
import com.glowxq.triflow.base.ai.mapper.AiCallLogMapper;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsDistributionVO;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsDistributionVO.DistributionItem;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsOverviewVO;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsTrendVO;
import com.glowxq.triflow.base.analytics.service.AnalyticsService;
import com.glowxq.triflow.base.system.entity.SysRole;
import com.glowxq.triflow.base.system.mapper.SysRoleMapper;
import com.glowxq.triflow.base.system.mapper.SysUserMapper;
import com.glowxq.triflow.base.system.mapper.SysUserRoleMapper;
import com.glowxq.triflow.base.wallet.enums.WalletAction;
import com.glowxq.triflow.base.wallet.enums.WalletRechargeStatus;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import com.glowxq.triflow.base.wallet.mapper.WalletRechargeOrderMapper;
import com.glowxq.triflow.base.wallet.mapper.WalletTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据分析服务实现
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final WalletTransactionMapper transactionMapper;
    private final WalletRechargeOrderMapper rechargeOrderMapper;
    private final AiCallLogMapper aiCallLogMapper;

    @Override
    public AnalyticsOverviewVO getOverview() {
        AnalyticsOverviewVO vo = new AnalyticsOverviewVO();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        // 用户统计
        vo.setTotalUsers(userMapper.countActiveUsers());
        vo.setTodayNewUsers(userMapper.countActiveUsersBetween(todayStart, todayEnd));

        // 订单统计
        vo.setTotalOrders(rechargeOrderMapper.countActiveOrders());
        vo.setTodayOrders(rechargeOrderMapper.countActiveOrdersBetween(todayStart, todayEnd));

        // 交易流水统计（余额类型，收入操作）
        BigDecimal totalAmount = transactionMapper.sumAmountByTypeAndAction(
                WalletType.BALANCE.getCode(),
                WalletAction.INCOME.getCode()
        );
        vo.setTotalTransactionAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);

        BigDecimal todayAmount = transactionMapper.sumAmountByTypeAndActionBetween(
                WalletType.BALANCE.getCode(),
                WalletAction.INCOME.getCode(),
                todayStart,
                todayEnd
        );
        vo.setTodayTransactionAmount(todayAmount != null ? todayAmount : BigDecimal.ZERO);

        // 积分发放统计
        BigDecimal totalPoints = transactionMapper.sumAmountByTypeAndAction(
                WalletType.POINTS.getCode(),
                WalletAction.INCOME.getCode()
        );
        vo.setTotalPointsIssued(totalPoints != null ? totalPoints.longValue() : 0L);

        BigDecimal todayPoints = transactionMapper.sumAmountByTypeAndActionBetween(
                WalletType.POINTS.getCode(),
                WalletAction.INCOME.getCode(),
                todayStart,
                todayEnd
        );
        vo.setTodayPointsIssued(todayPoints != null ? todayPoints.longValue() : 0L);

        // AI 使用统计（仅统计成功调用）
        Integer successStatus = AiCallStatusEnum.SUCCESS.getValue();

        Long totalAiCalls = aiCallLogMapper.countByStatus(successStatus);
        Long todayAiCalls = aiCallLogMapper.countByStatusBetween(successStatus, todayStart, todayEnd);
        Long totalAiTokens = aiCallLogMapper.sumTotalTokensByStatus(successStatus);
        Long todayAiTokens = aiCallLogMapper.sumTotalTokensByStatusBetween(successStatus, todayStart, todayEnd);

        vo.setTotalAiCalls(totalAiCalls != null ? totalAiCalls : 0L);
        vo.setTodayAiCalls(todayAiCalls != null ? todayAiCalls : 0L);
        vo.setTotalAiTokens(totalAiTokens != null ? totalAiTokens : 0L);
        vo.setTodayAiTokens(todayAiTokens != null ? todayAiTokens : 0L);

        return vo;
    }

    @Override
    public AnalyticsTrendVO getTrends(int days) {
        AnalyticsTrendVO vo = new AnalyticsTrendVO();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<String> dates = new ArrayList<>();
        List<Long> userRegistrations = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        List<BigDecimal> transactionAmounts = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date.format(formatter));

            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            // 当天用户注册数
            Long userCount = userMapper.countActiveUsersBetween(dayStart, dayEnd);
            userRegistrations.add(userCount != null ? userCount : 0L);

            // 当天订单数
            Long orderCount = rechargeOrderMapper.countActiveOrdersBetween(dayStart, dayEnd);
            orderCounts.add(orderCount != null ? orderCount : 0L);

            // 当天交易额
            BigDecimal amount = transactionMapper.sumAmountByTypeAndActionBetween(
                    WalletType.BALANCE.getCode(),
                    WalletAction.INCOME.getCode(),
                    dayStart,
                    dayEnd
            );
            transactionAmounts.add(amount != null ? amount : BigDecimal.ZERO);
        }

        vo.setDates(dates);
        vo.setUserRegistrations(userRegistrations);
        vo.setOrderCounts(orderCounts);
        vo.setTransactionAmounts(transactionAmounts);

        return vo;
    }

    @Override
    public AnalyticsDistributionVO getDistribution() {
        AnalyticsDistributionVO vo = new AnalyticsDistributionVO();

        // 用户角色分布
        List<DistributionItem> roleDistribution = new ArrayList<>();
        List<SysRole> roles = roleMapper.selectNotDeleted();

        for (SysRole role : roles) {
            Long count = userRoleMapper.countByRoleId(role.getId());
            if (count > 0) {
                roleDistribution.add(new DistributionItem(role.getRoleName(), count));
            }
        }
        vo.setUserRoleDistribution(roleDistribution);

        // 交易类型分布
        List<DistributionItem> transactionDistribution = new ArrayList<>();
        for (WalletType type : WalletType.values()) {
            Long count = transactionMapper.countActiveByType(type.getCode());
            if (count > 0) {
                transactionDistribution.add(new DistributionItem(type.getName(), count));
            }
        }
        vo.setTransactionTypeDistribution(transactionDistribution);

        // 订单状态分布
        List<DistributionItem> orderDistribution = new ArrayList<>();
        for (WalletRechargeStatus status : WalletRechargeStatus.values()) {
            Long count = rechargeOrderMapper.countActiveOrdersByStatus(status.getCode());
            if (count > 0) {
                orderDistribution.add(new DistributionItem(status.getName(), count));
            }
        }
        vo.setOrderStatusDistribution(orderDistribution);

        return vo;
    }
}
