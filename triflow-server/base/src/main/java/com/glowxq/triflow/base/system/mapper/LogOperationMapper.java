package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.LogOperation;
import com.glowxq.triflow.base.system.pojo.dto.LogOperationQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 操作日志数据访问层
 * <p>
 * 提供操作日志的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Mapper
public interface LogOperationMapper extends BaseMapper<LogOperation> {

    /**
     * 根据查询条件分页查询操作日志
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    default Page<LogOperation> paginateByQuery(int pageNum, int pageSize, LogOperationQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 根据查询条件查询操作日志列表
     *
     * @param query 查询条件
     * @return 日志列表
     */
    default List<LogOperation> selectByQuery(LogOperationQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 清空所有操作日志
     *
     * @return 删除行数
     */
    default long deleteAll() {
        QueryWrapper qw = QueryWrapper.create().from(LogOperation.class);
        return deleteByQuery(qw);
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(LogOperationQueryDTO query) {
        return QueryWrapper.create()
                           .from(LogOperation.class)
                           .like(LogOperation::getDescription, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(LogOperation::getModule, query.getModule(), StringUtils.isNotBlank(query.getModule()))
                           .eq(LogOperation::getOperation, query.getOperation(), StringUtils.isNotBlank(query.getOperation()))
                           .eq(LogOperation::getStatus, query.getStatus(), query.getStatus() != null)
                           .ge(LogOperation::getOperateTime, query.getStartTime(), query.getStartTime() != null)
                           .le(LogOperation::getOperateTime, query.getEndTime(), query.getEndTime() != null)
                           .orderBy(LogOperation::getOperateTime, false);
    }

}
