package com.glowxq.triflow.base.ai.mapper;

import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.ai.entity.AiCallLog;
import com.glowxq.triflow.base.ai.pojo.query.AiCallLogQuery;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * AI 调用记录 Mapper
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Mapper
public interface AiCallLogMapper extends BaseMapper<AiCallLog> {

    /**
     * 根据查询条件分页查询
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    default Page<AiCallLog> paginateByQuery(int pageNum, int pageSize, AiCallLogQuery query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 统计 AI 调用次数
     *
     * @param status 调用状态（可选）
     * @return 调用次数
     */
    default Long countByStatus(Integer status) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(AiCallLog.class)
                                      .eq(AiCallLog::getDeleted, BooleanEnum.NO.getValue())
                                      .eq(AiCallLog::getStatus, status, status != null);
        return selectCountByQuery(qw);
    }

    /**
     * 统计指定时间范围内的 AI 调用次数
     *
     * @param status    调用状态（可选）
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 调用次数
     */
    default Long countByStatusBetween(Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(AiCallLog.class)
                                      .eq(AiCallLog::getDeleted, BooleanEnum.NO.getValue())
                                      .eq(AiCallLog::getStatus, status, status != null)
                                      .between(AiCallLog::getCreateTime, startTime, endTime);
        return selectCountByQuery(qw);
    }

    /**
     * 统计 token 消耗总量
     *
     * @param status 调用状态（可选）
     * @return token 总量
     */
    default Long sumTotalTokensByStatus(Integer status) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select("COALESCE(SUM(total_tokens), 0) as total")
                                      .from(AiCallLog.class)
                                      .eq(AiCallLog::getDeleted, BooleanEnum.NO.getValue())
                                      .eq(AiCallLog::getStatus, status, status != null);
        return selectObjectByQueryAs(qw, Long.class);
    }

    /**
     * 统计指定时间范围内的 token 消耗总量
     *
     * @param status    调用状态（可选）
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return token 总量
     */
    default Long sumTotalTokensByStatusBetween(Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select("COALESCE(SUM(total_tokens), 0) as total")
                                      .from(AiCallLog.class)
                                      .eq(AiCallLog::getDeleted, BooleanEnum.NO.getValue())
                                      .eq(AiCallLog::getStatus, status, status != null)
                                      .between(AiCallLog::getCreateTime, startTime, endTime);
        return selectObjectByQueryAs(qw, Long.class);
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(AiCallLogQuery query) {
        return QueryWrapper.create()
                .from(AiCallLog.class)
                .eq(AiCallLog::getDeleted, 0)
                .eq(AiCallLog::getUserId, query.getUserId(), query.getUserId() != null)
                .like(AiCallLog::getUsername, query.getUsername(), StringUtils.isNotBlank(query.getUsername()))
                .eq(AiCallLog::getProvider, query.getProvider(), StringUtils.isNotBlank(query.getProvider()))
                .eq(AiCallLog::getModel, query.getModel(), StringUtils.isNotBlank(query.getModel()))
                .eq(AiCallLog::getStatus, query.getStatus(), query.getStatus() != null)
                .ge(AiCallLog::getCreateTime, query.getStartTime(), query.getStartTime() != null)
                .le(AiCallLog::getCreateTime, query.getEndTime(), query.getEndTime() != null)
                .orderBy(AiCallLog::getCreateTime, false);
    }
}
