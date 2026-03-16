package com.glowxq.triflow.base.wallet.mapper;

import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.wallet.entity.WalletRechargeOrder;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeOrderQuery;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 钱包充值订单 Mapper
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Mapper
public interface WalletRechargeOrderMapper extends BaseMapper<WalletRechargeOrder> {

    default Page<WalletRechargeOrder> paginateByQuery(int pageNum, int pageSize, WalletRechargeOrderQuery query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    default Page<WalletRechargeOrder> paginateByQueryAndUserIds(int pageNum, int pageSize,
                                                                WalletRechargeOrderQuery query,
                                                                List<Long> userIds) {
        QueryWrapper wrapper = buildBaseQueryWrapper(query);
        if (CollectionUtils.isNotEmpty(userIds)) {
            wrapper.in(WalletRechargeOrder::getUserId, userIds);
        } else if (StringUtils.isNotBlank(query.getUsername())) {
            wrapper.eq(WalletRechargeOrder::getUserId, -1L);
        }
        wrapper.orderBy(WalletRechargeOrder::getCreateTime, false);
        return paginate(pageNum, pageSize, wrapper);
    }

    default WalletRechargeOrder selectByOrderNo(String orderNo) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .from(WalletRechargeOrder.class)
                                           .eq(WalletRechargeOrder::getOrderNo, orderNo);
        return selectOneByQuery(wrapper);
    }

    /**
     * 统计未删除订单总数
     *
     * @return 订单总数
     */
    default Long countActiveOrders() {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .from(WalletRechargeOrder.class)
                                           .eq(WalletRechargeOrder::getDeleted, BooleanEnum.NO.getValue());
        return selectCountByQuery(wrapper);
    }

    /**
     * 统计指定时间范围内的订单数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 订单数量
     */
    default Long countActiveOrdersBetween(LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .from(WalletRechargeOrder.class)
                                           .eq(WalletRechargeOrder::getDeleted, BooleanEnum.NO.getValue())
                                           .between(WalletRechargeOrder::getCreateTime, startTime, endTime);
        return selectCountByQuery(wrapper);
    }

    /**
     * 按状态统计订单数量
     *
     * @param status 订单状态
     * @return 订单数量
     */
    default Long countActiveOrdersByStatus(String status) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .from(WalletRechargeOrder.class)
                                           .eq(WalletRechargeOrder::getDeleted, BooleanEnum.NO.getValue())
                                           .eq(WalletRechargeOrder::getStatus, status, StringUtils.isNotBlank(status));
        return selectCountByQuery(wrapper);
    }

    default QueryWrapper buildQueryWrapper(WalletRechargeOrderQuery query) {
        QueryWrapper wrapper = buildBaseQueryWrapper(query);
        wrapper.orderBy(WalletRechargeOrder::getCreateTime, false);
        return wrapper;
    }

    default QueryWrapper buildBaseQueryWrapper(WalletRechargeOrderQuery query) {
        return QueryWrapper.create()
                           .from(WalletRechargeOrder.class)
                           .eq(WalletRechargeOrder::getUserId, query.getUserId(), query.getUserId() != null)
                           .eq(WalletRechargeOrder::getType, query.getType(), StringUtils.isNotBlank(query.getType()))
                           .eq(WalletRechargeOrder::getStatus, query.getStatus(), StringUtils.isNotBlank(query.getStatus()))
                           .like(WalletRechargeOrder::getOrderNo, query.getOrderNo(), StringUtils.isNotBlank(query.getOrderNo()))
                           .ge(WalletRechargeOrder::getCreateTime, query.getStartTime(), query.getStartTime() != null)
                           .le(WalletRechargeOrder::getCreateTime, query.getEndTime(), query.getEndTime() != null);
    }
}
