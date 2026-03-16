package com.glowxq.triflow.base.system.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysConfig;
import com.glowxq.triflow.base.system.mapper.SysConfigMapper;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysConfigVO;
import com.glowxq.triflow.base.system.service.SysConfigService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务实现类
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper configMapper;

    /** 配置缓存 */
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysConfigCreateDTO dto) {
        // 检查配置键是否存在
        if (configMapper.existsByConfigKey(dto.getConfigKey(), null)) {
            throw new BusinessException("配置键已存在");
        }

        // DTO → Entity 转换
        SysConfig config = MapStructUtils.convert(dto, SysConfig.class);

        // 设置默认值
        if (config.getValueType() == null) {
            config.setValueType("string");
        }
        if (config.getConfigType() == null) {
            config.setConfigType(1);
        }
        if (config.getSort() == null) {
            config.setSort(0);
        }
        if (config.getStatus() == null) {
            config.setStatus(1);
        }

        // 持久化
        configMapper.insert(config);

        // 更新缓存
        if (config.getStatus() == 1) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }

        log.info("创建配置成功, id={}, key={}", config.getId(), config.getConfigKey());
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysConfigUpdateDTO dto) {
        SysConfig existingConfig = configMapper.selectOneById(dto.getId());
        if (existingConfig == null) {
            log.warn("更新配置失败, 配置不存在, id={}", dto.getId());
            return false;
        }

        // 检查配置键唯一性
        if (StringUtils.isNotBlank(dto.getConfigKey())
            && configMapper.existsByConfigKey(dto.getConfigKey(), dto.getId())) {
            throw new BusinessException("配置键已存在");
        }

        // 检查是否为系统内置配置
        if (existingConfig.getConfigType() == 0 && dto.getConfigKey() != null
            && !existingConfig.getConfigKey().equals(dto.getConfigKey())) {
            throw new BusinessException("系统内置配置的键不可修改");
        }

        // DTO → Entity 转换
        SysConfig config = MapStructUtils.convert(dto, SysConfig.class);

        // 执行更新
        int rows = configMapper.update(config);

        // 更新缓存
        SysConfig updated = configMapper.selectOneById(dto.getId());
        if (updated != null) {
            if (updated.getStatus() == 1) {
                configCache.put(updated.getConfigKey(), updated.getConfigValue());
            } else {
                configCache.remove(updated.getConfigKey());
            }
        }

        log.info("更新配置, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    public SysConfigVO getById(Long id) {
        SysConfig config = configMapper.selectOneById(id);
        if (config == null) {
            return null;
        }
        return MapStructUtils.convert(config, SysConfigVO.class);
    }

    @Override
    public SysConfig getByConfigKey(String configKey) {
        return configMapper.selectByConfigKey(configKey);
    }

    @Override
    public String getValueByKey(String configKey) {
        return getValueByKey(configKey, null);
    }

    @Override
    public String getValueByKey(String configKey, String defaultValue) {
        // 先从缓存获取
        String value = configCache.get(configKey);
        if (value != null) {
            return value;
        }

        // 缓存未命中，从数据库获取
        SysConfig config = configMapper.selectByConfigKey(configKey);
        if (config != null && config.getStatus() == 1) {
            configCache.put(configKey, config.getConfigValue());
            return config.getConfigValue();
        }

        return defaultValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        SysConfig config = configMapper.selectOneById(id);
        if (config == null) {
            return false;
        }

        // // 检查是否为系统内置配置
        // if (config.getConfigType() == 0) {
        //     throw new BusinessException("系统内置配置不可删除");
        // }

        // 删除配置
        int rows = configMapper.deleteById(id);

        // 移除缓存
        configCache.remove(config.getConfigKey());

        log.info("删除配置, id={}, key={}, affected={}", id, config.getConfigKey(), rows);
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
    public List<SysConfigVO> list(SysConfigQueryDTO query) {
        List<SysConfig> configs = configMapper.selectByQuery(query);
        return MapStructUtils.convert(configs, SysConfigVO.class);
    }

    @Override
    public Page<SysConfigVO> page(SysConfigQueryDTO query) {
        QueryWrapper qw = configMapper.buildQueryWrapper(query);
        Page<SysConfigVO> page = query.buildPage();
        return configMapper.paginateAs(page, qw, SysConfigVO.class);
    }

    @Override
    public List<SysConfigVO> listByCategory(String category) {
        List<SysConfig> configs = configMapper.selectByCategory(category);
        return MapStructUtils.convert(configs, SysConfigVO.class);
    }

    @Override
    public List<SysConfigVO> listAllEnabled() {
        List<SysConfig> configs = configMapper.selectAllEnabled();
        return MapStructUtils.convert(configs, SysConfigVO.class);
    }

    @Override
    public void refreshCache() {
        configCache.clear();
        List<SysConfig> configs = configMapper.selectAllEnabled();
        for (SysConfig config : configs) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }
        log.info("刷新配置缓存完成, 共加载 {} 条配置", configs.size());
    }

}
