package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysSwitch;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统开关数据访问层
 * <p>
 * 提供系统开关的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Mapper
public interface SysSwitchMapper extends BaseMapper<SysSwitch> {

    /**
     * 根据开关键查询开关
     *
     * @param switchKey 开关键
     * @return 开关信息
     */
    default SysSwitch selectBySwitchKey(String switchKey) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitch.class)
                                      .eq(SysSwitch::getSwitchKey, switchKey);
        return selectOneByQuery(qw);
    }

    /**
     * 检查开关键是否存在
     *
     * @param switchKey 开关键
     * @param excludeId 排除的开关ID
     * @return true 表示存在
     */
    default boolean existsBySwitchKey(String switchKey, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitch.class)
                                      .eq(SysSwitch::getSwitchKey, switchKey)
                                      .ne(SysSwitch::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 根据分类查询开关列表
     *
     * @param category 分类
     * @return 开关列表
     */
    default List<SysSwitch> selectByCategory(String category) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitch.class)
                                      .eq(SysSwitch::getCategory, category)
                                      .orderBy(SysSwitch::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 查询所有开启的开关
     *
     * @return 开关列表
     */
    default List<SysSwitch> selectAllEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitch.class)
                                      .eq(SysSwitch::getSwitchValue, 1)
                                      .orderBy(SysSwitch::getCategory, true)
                                      .orderBy(SysSwitch::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 根据开关类型查询开关列表
     *
     * @param switchType 开关类型
     * @return 开关列表
     */
    default List<SysSwitch> selectBySwitchType(String switchType) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysSwitch.class)
                                      .eq(SysSwitch::getSwitchType, switchType)
                                      .orderBy(SysSwitch::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 根据查询条件查询开关列表
     *
     * @param query 查询条件
     * @return 开关列表
     */
    default List<SysSwitch> selectByQuery(SysSwitchQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据查询条件分页查询开关
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    default Page<SysSwitch> paginateByQuery(int pageNum, int pageSize, SysSwitchQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(SysSwitchQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysSwitch.class)
                           .and(wrapper -> wrapper
                                   .like(SysSwitch::getSwitchName, query.getKeyword())
                                   .or(SysSwitch::getSwitchKey).like(query.getKeyword()),
                                   StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysSwitch::getSwitchKey, query.getSwitchKey(), StringUtils.isNotBlank(query.getSwitchKey()))
                           .eq(SysSwitch::getSwitchType, query.getSwitchType(), StringUtils.isNotBlank(query.getSwitchType()))
                           .eq(SysSwitch::getCategory, query.getCategory(), StringUtils.isNotBlank(query.getCategory()))
                           .eq(SysSwitch::getSwitchValue, query.getSwitchValue(), query.getSwitchValue() != null)
                           .orderBy(SysSwitch::getCategory, true)
                           .orderBy(SysSwitch::getSort, true);
    }

}
