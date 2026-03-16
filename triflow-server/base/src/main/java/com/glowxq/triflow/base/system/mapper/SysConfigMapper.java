package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysConfig;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统配置数据访问层
 * <p>
 * 提供系统配置的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    default SysConfig selectByConfigKey(String configKey) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysConfig.class)
                                      .eq(SysConfig::getConfigKey, configKey);
        return selectOneByQuery(qw);
    }

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @param excludeId 排除的配置ID
     * @return true 表示存在
     */
    default boolean existsByConfigKey(String configKey, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysConfig.class)
                                      .eq(SysConfig::getConfigKey, configKey)
                                      .ne(SysConfig::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 根据分类查询配置列表
     *
     * @param category 分类
     * @return 配置列表
     */
    default List<SysConfig> selectByCategory(String category) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysConfig.class)
                                      .eq(SysConfig::getCategory, category)
                                      .eq(SysConfig::getStatus, 1)
                                      .orderBy(SysConfig::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 查询所有启用的配置
     *
     * @return 配置列表
     */
    default List<SysConfig> selectAllEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysConfig.class)
                                      .eq(SysConfig::getStatus, 1)
                                      .orderBy(SysConfig::getCategory, true)
                                      .orderBy(SysConfig::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 根据查询条件查询配置列表
     *
     * @param query 查询条件
     * @return 配置列表
     */
    default List<SysConfig> selectByQuery(SysConfigQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据查询条件分页查询配置
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    default Page<SysConfig> paginateByQuery(int pageNum, int pageSize, SysConfigQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(SysConfigQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysConfig.class)
                           .like(SysConfig::getConfigName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysConfig::getConfigType, query.getConfigType(), query.getConfigType() != null)
                           .eq(SysConfig::getCategory, query.getCategory(), StringUtils.isNotBlank(query.getCategory()))
                           .eq(SysConfig::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(SysConfig::getCategory, true)
                           .orderBy(SysConfig::getSort, true);
    }

}
