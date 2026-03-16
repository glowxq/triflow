package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.entity.LogOperation;
import com.glowxq.triflow.base.system.pojo.dto.LogOperationQueryDTO;
import com.glowxq.triflow.base.system.pojo.vo.LogOperationVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 操作日志服务接口
 * <p>
 * 定义操作日志业务逻辑的契约，Service 层只负责业务逻辑编排，
 * 不包含任何 SQL 或 QueryWrapper 逻辑（这些封装在 Mapper 层）。
 * </p>
 *
 * @author glowxq
 * @since 2026-01-29
 */
public interface LogOperationService {

    /**
     * 分页查询操作日志
     *
     * @param query 查询条件（包含分页参数）
     * @return 分页结果
     */
    Page<LogOperationVO> page(LogOperationQueryDTO query);

    /**
     * 根据ID获取日志详情
     *
     * @param id 日志ID
     * @return 日志详情 VO
     */
    LogOperationVO getById(Long id);

    /**
     * 批量删除操作日志
     *
     * @param ids 日志ID列表
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids);

    /**
     * 清空所有操作日志
     *
     * @return 删除的记录数
     */
    long clear();

    /**
     * 根据查询条件查询日志列表（用于导出）
     *
     * @param query 查询条件
     * @return 日志列表
     */
    List<LogOperationVO> list(LogOperationQueryDTO query);

    /**
     * 异步保存操作日志
     *
     * @param logOperation 操作日志实体
     */
    void asyncSave(LogOperation logOperation);

}
