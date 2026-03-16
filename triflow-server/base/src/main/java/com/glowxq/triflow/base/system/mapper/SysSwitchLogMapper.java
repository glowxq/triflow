package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysSwitchLog;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 开关操作日志数据访问层
 * <p>
 * 提供开关操作日志的数据库操作方法。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Mapper
public interface SysSwitchLogMapper extends BaseMapper<SysSwitchLog> {

    /**
     * 根据开关ID查询日志列表
     *
     * @param switchId 开关ID
     * @return 日志列表
     */
    default List<SysSwitchLog> selectBySwitchId(Long switchId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitchLog.class)
                                      .eq(SysSwitchLog::getSwitchId, switchId)
                                      .orderBy(SysSwitchLog::getOperateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据开关键查询日志列表
     *
     * @param switchKey 开关键
     * @return 日志列表
     */
    default List<SysSwitchLog> selectBySwitchKey(String switchKey) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitchLog.class)
                                      .eq(SysSwitchLog::getSwitchKey, switchKey)
                                      .orderBy(SysSwitchLog::getOperateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 分页查询开关日志
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param switchId 开关ID
     * @return 分页结果
     */
    default Page<SysSwitchLog> paginateBySwitchId(int pageNum, int pageSize, Long switchId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitchLog.class)
                                      .eq(SysSwitchLog::getSwitchId, switchId, switchId != null)
                                      .orderBy(SysSwitchLog::getOperateTime, false);
        return paginate(pageNum, pageSize, qw);
    }

    /**
     * 删除指定开关的所有日志
     *
     * @param switchId 开关ID
     * @return 删除的记录数
     */
    default int deleteBySwitchId(Long switchId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitchLog.class)
                                      .eq(SysSwitchLog::getSwitchId, switchId);
        return deleteByQuery(qw);
    }

}
