package com.glowxq.triflow.base.config;

import com.glowxq.common.core.common.entity.DictVO;
import com.glowxq.common.core.common.service.DictService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典服务默认实现
 * <p>
 * 提供基础的字典服务实现，暂时返回空字典。
 * 后续可扩展为从数据库或缓存中获取字典数据。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Service
public class DictServiceImpl implements DictService {

    @Override
    public String getDictLabel(String dictType, String dictValue, String separator) {
        // 暂时返回原值，后续可从数据库获取
        return dictValue;
    }

    @Override
    public String getDictValue(String dictType, String dictLabel, String separator) {
        // 暂时返回原值，后续可从数据库获取
        return dictLabel;
    }

    @Override
    public Map<String, String> getAllDictByType(String dictType) {
        // 暂时返回空Map，后续可从数据库获取
        return new HashMap<>();
    }

    @Override
    public Map<String, List<DictVO>> getAllDict() {
        // 暂时返回空Map，后续可从数据库获取
        return Collections.emptyMap();
    }
}
