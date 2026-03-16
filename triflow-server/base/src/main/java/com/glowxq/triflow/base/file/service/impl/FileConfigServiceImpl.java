package com.glowxq.triflow.base.file.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.file.entity.FileConfig;
import com.glowxq.triflow.base.file.mapper.FileConfigMapper;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigCreateDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigQueryDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigUpdateDTO;
import com.glowxq.triflow.base.file.pojo.vo.FileConfigVO;
import com.glowxq.triflow.base.file.service.FileConfigService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件存储配置服务实现类
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileConfigServiceImpl implements FileConfigService {

    private final FileConfigMapper configMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FileConfigCreateDTO dto) {
        if (configMapper.existsByConfigKey(dto.getConfigKey(), null)) {
            throw new BusinessException("配置标识已存在");
        }

        FileConfig config = MapStructUtils.convert(dto, FileConfig.class);

        if (config.getUseHttps() == null) {
            config.setUseHttps(1);
        }
        if (config.getDefaulted() == null) {
            config.setDefaulted(0);
        }
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        if (StringUtils.isBlank(config.getBasePath())) {
            config.setBasePath("");
        }

        // 如果设为默认，先清除其他默认配置
        if (config.getDefaulted() == 1) {
            configMapper.clearDefaultFlag();
        }

        configMapper.insert(config);
        log.info("创建文件存储配置成功, id={}, key={}", config.getId(), config.getConfigKey());
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(FileConfigUpdateDTO dto) {
        FileConfig existing = configMapper.selectOneById(dto.getId());
        if (existing == null) {
            return false;
        }

        if (StringUtils.isNotBlank(dto.getConfigKey())
            && configMapper.existsByConfigKey(dto.getConfigKey(), dto.getId())) {
            throw new BusinessException("配置标识已存在");
        }

        // 如果设为默认，先清除其他默认配置
        if (dto.getDefaulted() != null && dto.getDefaulted() == 1) {
            configMapper.clearDefaultFlag();
        }

        FileConfig config = MapStructUtils.convert(dto, FileConfig.class);
        int rows = configMapper.update(config);
        log.info("更新文件存储配置, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    public FileConfigVO getById(Long id) {
        FileConfig config = configMapper.selectOneById(id);
        if (config == null) {
            return null;
        }
        return MapStructUtils.convert(config, FileConfigVO.class);
    }

    @Override
    public FileConfig getByConfigKey(String configKey) {
        return configMapper.selectByConfigKey(configKey);
    }

    @Override
    public FileConfig getDefaultConfig() {
        return configMapper.selectDefaultConfig();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        FileConfig config = configMapper.selectOneById(id);
        if (config == null) {
            return false;
        }
        if (config.getDefaulted() == 1) {
            throw new BusinessException("默认配置不可删除");
        }
        int rows = configMapper.deleteById(id);
        log.info("删除文件存储配置, id={}, affected={}", id, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public List<FileConfigVO> list(FileConfigQueryDTO query) {
        List<FileConfig> configs = configMapper.selectByQuery(query);
        return MapStructUtils.convert(configs, FileConfigVO.class);
    }

    @Override
    public Page<FileConfigVO> page(FileConfigQueryDTO query) {
        Page<FileConfig> configPage = configMapper.paginateByQuery(query.getPageNum(), query.getPageSize(), query);
        Page<FileConfigVO> voPage = new Page<>();
        voPage.setPageNumber(configPage.getPageNumber());
        voPage.setPageSize(configPage.getPageSize());
        voPage.setTotalPage(configPage.getTotalPage());
        voPage.setTotalRow(configPage.getTotalRow());
        voPage.setRecords(MapStructUtils.convert(configPage.getRecords(), FileConfigVO.class));
        return voPage;
    }

    @Override
    public List<FileConfigVO> listAllEnabled() {
        List<FileConfig> configs = configMapper.selectAllEnabled();
        return MapStructUtils.convert(configs, FileConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefault(Long id) {
        FileConfig config = configMapper.selectOneById(id);
        if (config == null) {
            return false;
        }
        if (config.getStatus() != 1) {
            throw new BusinessException("禁用状态的配置不能设为默认");
        }
        configMapper.clearDefaultFlag();
        FileConfig update = new FileConfig();
        update.setId(id);
        update.setDefaulted(1);
        int rows = configMapper.update(update);
        log.info("设置默认文件存储配置, id={}", id);
        return rows > 0;
    }

}
