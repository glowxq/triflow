package com.glowxq.triflow.base.wallet.service.impl;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.glowxq.common.core.common.enums.SocialType;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.common.wechat.pay.pojo.WechatPaymentData;
import com.glowxq.common.wechat.pay.service.WechatPaymentService;
import com.glowxq.common.wechat.pay.utils.PaymentUtils;
import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.entity.SysUserSocial;
import com.glowxq.triflow.base.system.mapper.SysUserMapper;
import com.glowxq.triflow.base.system.service.SysUserSocialService;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeConfig;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeOrder;
import com.glowxq.triflow.base.wallet.enums.WalletRechargeStatus;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import com.glowxq.triflow.base.wallet.mapper.WalletRechargeConfigMapper;
import com.glowxq.triflow.base.wallet.mapper.WalletRechargeOrderMapper;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeCreateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeOrderUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeRefundDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeOrderQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeOrderVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargePayVO;
import com.glowxq.triflow.base.wallet.service.WalletRechargeConfigService;
import com.glowxq.triflow.base.wallet.service.WalletRechargeOrderService;
import com.glowxq.triflow.base.wallet.service.WalletService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 钱包充值订单服务实现
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletRechargeOrderServiceImpl implements WalletRechargeOrderService {

    private static final DateTimeFormatter ORDER_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter REFUND_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final WalletRechargeOrderMapper orderMapper;
    private final WalletRechargeConfigService configService;
    private final WalletRechargeConfigMapper configMapper;
    private final WechatPaymentService paymentService;
    private final SysUserSocialService userSocialService;
    private final SysUserMapper userMapper;
    private final WalletService walletService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletRechargePayVO createOrder(WalletRechargeCreateDTO dto) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        WalletRechargeConfig config = configService.getEntityById(dto.getConfigId());
        if (config == null || config.getDeleted() != null && config.getDeleted() == 1) {
            throw new BusinessException("充值配置不存在");
        }
        if (config.getStatus() == null || config.getStatus() != 1) {
            throw new BusinessException("充值配置已禁用");
        }

        WalletType walletType = WalletType.fromCode(config.getType());
        if (walletType == null) {
            throw new BusinessException("充值类型无效");
        }

        SysUserSocial social = userSocialService.getByUserIdAndType(userId, SocialType.WECHAT_MINIAPP.getCode());
        if (social == null || social.getSocialId() == null) {
            throw new BusinessException("请先绑定微信账号");
        }

        String orderNo = generateOrderNo();
        BigDecimal rewardAmount = config.getRewardAmount();
        if (config.getBonusAmount() != null) {
            rewardAmount = rewardAmount.add(config.getBonusAmount());
        }

        WalletRechargeOrder order = new WalletRechargeOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setConfigId(config.getId());
        order.setType(config.getType());
        order.setPayAmount(config.getPayAmount());
        order.setRewardAmount(rewardAmount);
        order.setStatus(WalletRechargeStatus.PENDING.getCode());
        orderMapper.insert(order);

        try {
            String desc = walletType == WalletType.POINTS ? "积分充值" : "余额充值";
            WechatPaymentData paymentData = paymentService.payment(orderNo, config.getPayAmount(), social.getSocialId(), desc);

            WalletRechargePayVO vo = new WalletRechargePayVO();
            vo.setOrderNo(orderNo);
            vo.setType(config.getType());
            vo.setPayAmount(config.getPayAmount());
            vo.setRewardAmount(rewardAmount);
            vo.setPaymentData(paymentData);
            return vo;
        } catch (Exception e) {
            log.error("创建微信支付订单失败: {}", e.getMessage(), e);
            order.setStatus(WalletRechargeStatus.FAILED.getCode());
            orderMapper.update(order);
            throw new BusinessException("微信支付下单失败，请稍后再试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePayNotify(WxPayNotifyV3Result result) {
        if (result == null || result.getResult() == null) {
            throw new BusinessException("支付回调数据为空");
        }

        WxPayNotifyV3Result.DecryptNotifyResult notify = result.getResult();
        String orderNo = notify.getOutTradeNo();
        if (orderNo == null) {
            throw new BusinessException("支付回调订单号为空");
        }

        WalletRechargeOrder order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("支付回调订单不存在: {}", orderNo);
            return;
        }
        if (!WalletRechargeStatus.PENDING.getCode().equals(order.getStatus())) {
            return;
        }

        BigDecimal paidAmount = PaymentUtils.formatFightsToAmount(
            notify.getAmount() != null ? notify.getAmount().getTotal() : 0
        );
        if (order.getPayAmount() != null && order.getPayAmount().compareTo(paidAmount) != 0) {
            log.warn("支付金额不一致: orderNo={}, expected={}, actual={}", orderNo, order.getPayAmount(), paidAmount);
        }

        order.setStatus(WalletRechargeStatus.PAID.getCode());
        order.setWechatTransactionId(notify.getTransactionId());
        order.setPayTime(LocalDateTime.now());
        orderMapper.update(order);

        WalletType walletType = WalletType.fromCode(order.getType());
        if (walletType == WalletType.POINTS) {
            walletService.addPoints(order.getUserId(), order.getRewardAmount().longValue(),
                "积分充值", "recharge", orderNo, "微信支付充值");
        } else if (walletType == WalletType.BALANCE) {
            walletService.addBalance(order.getUserId(), order.getRewardAmount(),
                "余额充值", "recharge", orderNo, "微信支付充值");
        }
    }

    @Override
    public Page<WalletRechargeOrderVO> page(WalletRechargeOrderQuery query) {
        Page<WalletRechargeOrder> page;
        if (StringUtils.isNotBlank(query.getUsername())) {
            List<SysUser> matchedUsers = userMapper.selectByKeyword(query.getUsername());
            List<Long> userIds = matchedUsers.stream().map(SysUser::getId).toList();
            page = orderMapper.paginateByQueryAndUserIds(
                query.getPageNum(), query.getPageSize(), query, userIds);
        } else {
            QueryWrapper qw = orderMapper.buildQueryWrapper(query);
            page = orderMapper.paginate(query.buildPage(), qw);
        }

        List<WalletRechargeOrderVO> records = enrichOrderList(page.getRecords());
        return new Page<>(records, page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    @Override
    public Page<WalletRechargeOrderVO> myOrders(WalletRechargeOrderQuery query) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        query.setUserId(userId);
        return page(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(WalletRechargeRefundDTO dto) {
        WalletRechargeOrder order = orderMapper.selectOneById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (WalletRechargeStatus.REFUNDED.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单已退款");
        }
        if (!WalletRechargeStatus.PAID.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单当前状态不支持退款");
        }
        if (order.getPayAmount() == null || order.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("订单支付金额异常");
        }
        if (order.getRewardAmount() == null || order.getRewardAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("订单到账金额异常");
        }

        WalletType walletType = WalletType.fromCode(order.getType());
        if (walletType == null) {
            throw new BusinessException("订单充值类型无效");
        }
        if (walletType == WalletType.POINTS && order.getRewardAmount().stripTrailingZeros().scale() > 0) {
            throw new BusinessException("积分到账金额异常");
        }

        // 扣减用户钱包（失败直接中断）
        if (walletType == WalletType.POINTS) {
            walletService.deductPoints(order.getUserId(), order.getRewardAmount().longValue(),
                "充值退款", "recharge_refund", order.getOrderNo(), buildRefundRemark(dto.getReason()));
        } else if (walletType == WalletType.BALANCE) {
            walletService.deductBalance(order.getUserId(), order.getRewardAmount(),
                "充值退款", "recharge_refund", order.getOrderNo(), buildRefundRemark(dto.getReason()));
        }

        String refundNo = generateRefundNo();
        try {
            paymentService.refund(order.getOrderNo(), refundNo, order.getPayAmount(), order.getPayAmount(), buildRefundRemark(dto.getReason()));
        } catch (Exception e) {
            log.error("微信退款失败: orderNo={}, message={}", order.getOrderNo(), e.getMessage(), e);
            throw new BusinessException("微信退款失败，请稍后再试");
        }

        order.setRefundNo(refundNo);
        order.setRefundAmount(order.getPayAmount());
        order.setRefundReason(StringUtils.defaultIfBlank(dto.getReason(), "管理员退款"));
        order.setRefundTime(LocalDateTime.now());
        order.setRefundBy(LoginUtils.getUserId());
        order.setStatus(WalletRechargeStatus.REFUNDED.getCode());
        orderMapper.update(order);
    }

    @Override
    public boolean update(WalletRechargeOrderUpdateDTO dto) {
        WalletRechargeOrder order = orderMapper.selectOneById(dto.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (dto.getStatus() != null) {
            WalletRechargeStatus status = WalletRechargeStatus.fromCode(dto.getStatus());
            if (status == null) {
                throw new BusinessException("订单状态无效");
            }
            order.setStatus(status.getCode());
        }

        if (dto.getPayAmount() != null) {
            order.setPayAmount(dto.getPayAmount());
        }

        if (dto.getRewardAmount() != null) {
            order.setRewardAmount(dto.getRewardAmount());
        }

        if (dto.getRemark() != null) {
            order.setRemark(dto.getRemark());
        }

        return orderMapper.update(order) > 0;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(ORDER_TIME_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "WR" + timestamp + random;
    }

    private String generateRefundNo() {
        String timestamp = LocalDateTime.now().format(REFUND_TIME_FORMATTER);
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "RR" + timestamp + random;
    }

    private String buildRefundRemark(String reason) {
        return StringUtils.isNotBlank(reason) ? reason : "管理员退款";
    }

    private List<WalletRechargeOrderVO> enrichOrderList(List<WalletRechargeOrder> records) {
        if (CollectionUtils.isEmpty(records)) {
            return List.of();
        }

        List<Long> userIds = records.stream().map(WalletRechargeOrder::getUserId).distinct().toList();
        Map<Long, SysUser> userMap = userMapper.selectListByIds(userIds)
            .stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        List<Long> configIds = records.stream()
            .map(WalletRechargeOrder::getConfigId)
            .filter(id -> id != null)
            .distinct()
            .toList();
        Map<Long, WalletRechargeConfig> configMap = configIds.isEmpty()
            ? Map.of()
            : configMapper.selectListByIds(configIds)
                .stream()
                .collect(Collectors.toMap(WalletRechargeConfig::getId, c -> c));

        return records.stream().map(record -> {
            WalletRechargeOrderVO vo = MapStructUtils.convert(record, WalletRechargeOrderVO.class);
            SysUser user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
            }
            if (record.getConfigId() != null) {
                WalletRechargeConfig config = configMap.get(record.getConfigId());
                if (config != null) {
                    vo.setConfigName(config.getConfigName());
                }
            }
            WalletType type = WalletType.fromCode(record.getType());
            if (type != null) {
                vo.setTypeDesc(type.getName());
            }
            WalletRechargeStatus status = WalletRechargeStatus.fromCode(record.getStatus());
            if (status != null) {
                vo.setStatusDesc(status.getName());
            }
            return vo;
        }).toList();
    }
}
