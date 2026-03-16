package com.glowxq.triflow.base.ai.mapper;

import com.glowxq.triflow.base.ai.entity.SysAiConfig;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 配置 Mapper
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Mapper
public interface SysAiConfigMapper extends BaseMapper<SysAiConfig> {

    /**
     * 根据提供商编码查询配置
     *
     * @param provider 提供商编码
     * @return 配置记录
     */
    default SysAiConfig selectByProvider(String provider) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysAiConfig.class)
                                      .eq(SysAiConfig::getProvider, provider, StringUtils.isNotBlank(provider));
        return selectOneByQuery(qw);
    }
}
