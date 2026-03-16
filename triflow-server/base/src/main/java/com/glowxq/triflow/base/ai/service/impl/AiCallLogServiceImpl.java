package com.glowxq.triflow.base.ai.service.impl;

import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.ai.entity.AiCallLog;
import com.glowxq.triflow.base.ai.enums.AiCallStatusEnum;
import com.glowxq.triflow.base.ai.mapper.AiCallLogMapper;
import com.glowxq.triflow.base.ai.pojo.query.AiCallLogQuery;
import com.glowxq.triflow.base.ai.pojo.vo.AiCallLogVO;
import com.glowxq.triflow.base.ai.service.AiCallLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 调用记录服务实现
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCallLogServiceImpl implements AiCallLogService {

    /** 默认 Token 数 */
    private static final int DEFAULT_TOKENS = 0;

    /** 默认耗时 */
    private static final long DEFAULT_DURATION = 0L;

    /** 未删除状态 */
    private static final int NOT_DELETED = 0;

    /** 已删除状态 */
    private static final int DELETED = 1;

    private final AiCallLogMapper aiCallLogMapper;

    @Override
    public Page<AiCallLogVO> page(AiCallLogQuery query) {
        QueryWrapper qw = aiCallLogMapper.buildQueryWrapper(query);
        Page<AiCallLog> page = aiCallLogMapper.paginate(query.buildPage(), qw);

        List<AiCallLogVO> records = page.getRecords().stream()
                .map(this::toVO)
                .toList();

        return new Page<>(records, page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    @Override
    public AiCallLogVO getById(Long id) {
        AiCallLog entity = aiCallLogMapper.selectOneById(id);
        return entity != null ? toVO(entity) : null;
    }

    @Override
    public void saveLog(Long userId, String username, String provider, String model,
                        String systemPrompt, String userMessage, String aiResponse,
                        Integer promptTokens, Integer completionTokens, Integer totalTokens,
                        Long duration, Integer status, String errorMessage, String ip) {
        // 使用虚拟线程异步保存日志，比传统线程池更轻量高效
        Thread.startVirtualThread(() -> {
            try {
                AiCallLog entity = new AiCallLog();
                entity.setUserId(userId);
                entity.setUsername(username);
                entity.setProvider(provider);
                entity.setModel(model);
                entity.setSystemPrompt(systemPrompt);
                entity.setUserMessage(userMessage);
                entity.setAiResponse(aiResponse);
                entity.setPromptTokens(ObjectUtils.defaultIfNull(promptTokens, DEFAULT_TOKENS));
                entity.setCompletionTokens(ObjectUtils.defaultIfNull(completionTokens, DEFAULT_TOKENS));
                entity.setTotalTokens(ObjectUtils.defaultIfNull(totalTokens, DEFAULT_TOKENS));
                entity.setDuration(ObjectUtils.defaultIfNull(duration, DEFAULT_DURATION));
                entity.setStatus(status);
                entity.setErrorMessage(errorMessage);
                entity.setIp(ip);
                entity.setDeleted(NOT_DELETED);

                aiCallLogMapper.insert(entity);
                log.debug("AI 调用记录保存成功: userId={}, provider={}, model={}", userId, provider, model);
            } catch (Exception e) {
                log.error("AI 调用记录保存失败: userId={}, provider={}, error={}", userId, provider, e.getMessage(), e);
            }
        });
    }

    @Override
    public void delete(Long id) {
        AiCallLog entity = new AiCallLog();
        entity.setId(id);
        entity.setDeleted(DELETED);
        aiCallLogMapper.update(entity);
    }

    /**
     * 转换为 VO
     *
     * @param entity 实体
     * @return VO
     */
    private AiCallLogVO toVO(AiCallLog entity) {
        AiCallLogVO vo = MapStructUtils.convert(entity, AiCallLogVO.class);

        // 设置提供商名称 (重构后直接使用 provider 字符串)
        vo.setProviderName(entity.getProvider());

        // 设置状态描述
        AiCallStatusEnum statusEnum = AiCallStatusEnum.of(entity.getStatus());
        vo.setStatusDesc(statusEnum != null ? statusEnum.getName() : null);

        return vo;
    }
}
