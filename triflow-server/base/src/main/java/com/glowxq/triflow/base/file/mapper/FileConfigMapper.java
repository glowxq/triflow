package com.glowxq.triflow.base.file.mapper;

import com.glowxq.triflow.base.file.entity.FileConfig;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文件存储配置数据访问层
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Mapper
public interface FileConfigMapper extends BaseMapper<FileConfig> {

    /**
     * 根据配置标识查询配置
     */
    default FileConfig selectByConfigKey(String configKey) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileConfig.class)
                                      .eq(FileConfig::getConfigKey, configKey);
        return selectOneByQuery(qw);
    }

    /**
     * 检查配置标识是否存在
     */
    default boolean existsByConfigKey(String configKey, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileConfig.class)
                                      .eq(FileConfig::getConfigKey, configKey)
                                      .ne(FileConfig::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 获取默认配置
     */
    default FileConfig selectDefaultConfig() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileConfig.class)
                                      .eq(FileConfig::getDefaulted, 1)
                                      .eq(FileConfig::getStatus, 1);
        return selectOneByQuery(qw);
    }

    /**
     * 获取所有启用的配置
     */
    default List<FileConfig> selectAllEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileConfig.class)
                                      .eq(FileConfig::getStatus, 1)
                                      .orderBy(FileConfig::getDefaulted, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据存储类型查询配置
     */
    default List<FileConfig> selectByStorageType(String storageType) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileConfig.class)
                                      .eq(FileConfig::getStorageType, storageType)
                                      .eq(FileConfig::getStatus, 1);
        return selectListByQuery(qw);
    }

    /**
     * 根据查询条件查询配置列表
     */
    default List<FileConfig> selectByQuery(FileConfigQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据查询条件分页查询配置
     */
    default Page<FileConfig> paginateByQuery(int pageNum, int pageSize, FileConfigQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     */
    default QueryWrapper buildQueryWrapper(FileConfigQueryDTO query) {
        return QueryWrapper.create()
                           .from(FileConfig.class)
                           .like(FileConfig::getConfigName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(FileConfig::getStorageType, query.getStorageType(), StringUtils.isNotBlank(query.getStorageType()))
                           .eq(FileConfig::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(FileConfig::getDefaulted, false)
                           .orderBy(FileConfig::getCreateTime, false);
    }

    /**
     * 清除所有默认配置标记
     */
    default int clearDefaultFlag() {
        FileConfig update = new FileConfig();
        update.setDefaulted(0);
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileConfig.class)
                                      .eq(FileConfig::getDefaulted, 1);
        return updateByQuery(update, qw);
    }

}
