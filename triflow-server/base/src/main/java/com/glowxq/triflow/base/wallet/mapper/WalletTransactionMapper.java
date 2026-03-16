package com.glowxq.triflow.base.wallet.mapper;

import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.wallet.entity.WalletTransaction;
import com.glowxq.triflow.base.wallet.pojo.query.WalletTransactionQuery;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 钱包变动记录 Mapper
 * <p>
 * 提供钱包变动记录的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Mapper
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {

    /**
     * 根据查询条件分页查询变动记录
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    default Page<WalletTransaction> paginateByQuery(int pageNum, int pageSize, WalletTransactionQuery query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 根据查询条件查询变动记录列表
     *
     * @param query 查询条件
     * @return 变动记录列表
     */
    default List<WalletTransaction> selectByQuery(WalletTransactionQuery query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据用户ID列表查询变动记录（支持用户名模糊查询场景）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @param userIds  用户ID列表（从用户名模糊匹配得到）
     * @return 分页结果
     */
    default Page<WalletTransaction> paginateByQueryAndUserIds(int pageNum, int pageSize,
                                                               WalletTransactionQuery query,
                                                               List<Long> userIds) {
        QueryWrapper wrapper = buildBaseQueryWrapper(query);

        // 如果传入了用户ID列表，则限制在这些用户范围内
        if (CollectionUtils.isNotEmpty(userIds)) {
            wrapper.in(WalletTransaction::getUserId, userIds);
        } else if (StringUtils.isNotBlank(query.getUsername())) {
            // 如果有用户名查询但未匹配到用户，返回空结果
            wrapper.eq(WalletTransaction::getUserId, -1L);
        }

        wrapper.orderBy(WalletTransaction::getCreateTime, false);
        return paginate(pageNum, pageSize, wrapper);
    }

    /**
     * 根据用户ID查询变动记录
     *
     * @param userId 用户ID
     * @return 变动记录列表
     */
    default List<WalletTransaction> selectByUserId(Long userId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(WalletTransaction.class)
                                      .eq(WalletTransaction::getUserId, userId)
                                      .orderBy(WalletTransaction::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据业务类型和业务ID查询变动记录
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @return 变动记录列表
     */
    default List<WalletTransaction> selectByBiz(String bizType, String bizId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(WalletTransaction.class)
                                      .eq(WalletTransaction::getBizType, bizType, StringUtils.isNotBlank(bizType))
                                      .eq(WalletTransaction::getBizId, bizId, StringUtils.isNotBlank(bizId))
                                      .orderBy(WalletTransaction::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 统计指定类型的交易数量
     *
     * @param type 交易类型
     * @return 交易数量
     */
    default Long countActiveByType(String type) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(WalletTransaction.class)
                                      .eq(WalletTransaction::getDeleted, BooleanEnum.NO.getValue())
                                      .eq(WalletTransaction::getType, type, StringUtils.isNotBlank(type));
        return selectCountByQuery(qw);
    }

    /**
     * 按类型与操作统计金额汇总
     *
     * @param type   交易类型
     * @param action 操作类型
     * @return 汇总金额
     */
    default BigDecimal sumAmountByTypeAndAction(String type, String action) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select("COALESCE(SUM(amount), 0) as total")
                                      .from(WalletTransaction.class)
                                      .eq(WalletTransaction::getDeleted, BooleanEnum.NO.getValue())
                                      .eq(WalletTransaction::getType, type, StringUtils.isNotBlank(type))
                                      .eq(WalletTransaction::getAction, action, StringUtils.isNotBlank(action));
        return selectObjectByQueryAs(qw, BigDecimal.class);
    }

    /**
     * 按类型与操作统计指定时间范围内的金额汇总
     *
     * @param type      交易类型
     * @param action    操作类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 汇总金额
     */
    default BigDecimal sumAmountByTypeAndActionBetween(String type, String action,
                                                       LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select("COALESCE(SUM(amount), 0) as total")
                                      .from(WalletTransaction.class)
                                      .eq(WalletTransaction::getDeleted, BooleanEnum.NO.getValue())
                                      .eq(WalletTransaction::getType, type, StringUtils.isNotBlank(type))
                                      .eq(WalletTransaction::getAction, action, StringUtils.isNotBlank(action))
                                      .between(WalletTransaction::getCreateTime, startTime, endTime);
        return selectObjectByQueryAs(qw, BigDecimal.class);
    }

    /**
     * 构建完整查询条件（包含排序）
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(WalletTransactionQuery query) {
        QueryWrapper wrapper = buildBaseQueryWrapper(query);
        wrapper.orderBy(WalletTransaction::getCreateTime, false);
        return wrapper;
    }

    /**
     * 构建基础查询条件（不含排序，用于组合查询场景）
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildBaseQueryWrapper(WalletTransactionQuery query) {
        return QueryWrapper.create()
                           .from(WalletTransaction.class)
                           .eq(WalletTransaction::getUserId, query.getUserId(), query.getUserId() != null)
                           .eq(WalletTransaction::getType, query.getType(), StringUtils.isNotBlank(query.getType()))
                           .eq(WalletTransaction::getAction, query.getAction(), StringUtils.isNotBlank(query.getAction()))
                           .eq(WalletTransaction::getBizType, query.getBizType(), StringUtils.isNotBlank(query.getBizType()))
                           .eq(WalletTransaction::getBizId, query.getBizId(), StringUtils.isNotBlank(query.getBizId()))
                           .ge(WalletTransaction::getCreateTime, query.getStartTime(), query.getStartTime() != null)
                           .le(WalletTransaction::getCreateTime, query.getEndTime(), query.getEndTime() != null);
    }
}
