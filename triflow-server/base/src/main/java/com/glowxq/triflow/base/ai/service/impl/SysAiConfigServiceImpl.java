package com.glowxq.triflow.base.ai.service.impl;

import com.glowxq.common.ai.service.AiService;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.ai.entity.SysAiConfig;
import com.glowxq.triflow.base.ai.entity.SysPromptTemplate;
import com.glowxq.triflow.base.ai.mapper.SysAiConfigMapper;
import com.glowxq.triflow.base.ai.mapper.SysPromptTemplateMapper;
import com.glowxq.triflow.base.ai.pojo.dto.AiConfigSaveDTO;
import com.glowxq.triflow.base.ai.pojo.dto.PromptTemplateSaveDTO;
import com.glowxq.triflow.base.ai.pojo.query.PromptTemplateQuery;
import com.glowxq.triflow.base.ai.pojo.vo.AiConfigVO;
import com.glowxq.triflow.base.ai.pojo.vo.PromptTemplateVO;
import com.glowxq.triflow.base.ai.service.SysAiConfigService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 配置服务实现
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysAiConfigServiceImpl implements SysAiConfigService {

    private final SysAiConfigMapper configMapper;
    private final SysPromptTemplateMapper promptMapper;
    private final AiService aiService;

    // ==================== AI 配置 ====================

    @Override
    public List<AiConfigVO> getConfigList() {
        List<SysAiConfig> list = configMapper.selectAll();
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public AiConfigVO getConfig(Long id) {
        SysAiConfig config = configMapper.selectOneById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        return convertToVO(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveConfig(AiConfigSaveDTO dto) {
        SysAiConfig config;

        if (dto.getId() != null) {
            // 更新
            config = configMapper.selectOneById(dto.getId());
            if (config == null) {
                throw new BusinessException("配置不存在");
            }

            // 只有提供了新的 API Key 才更新
            if (StringUtils.isNotBlank(dto.getApiKey())) {
                config.setApiKey(dto.getApiKey());
            }
            config.setEndpoint(dto.getEndpoint());
            config.setDefaultModel(dto.getDefaultModel());
            config.setEnabled(dto.getEnabled());
            config.setTimeout(dto.getTimeout());
            config.setUpdateTime(LocalDateTime.now());

            configMapper.update(config);
        } else {
            // 新增
            if (StringUtils.isBlank(dto.getApiKey())) {
                throw new BusinessException("API Key 不能为空");
            }

            // 检查是否已存在该提供商的配置
            SysAiConfig existing = configMapper.selectByProvider(dto.getProvider());
            if (existing != null) {
                throw new BusinessException("该提供商的配置已存在");
            }

            config = new SysAiConfig();
            config.setProvider(dto.getProvider());
            config.setApiKey(dto.getApiKey());
            config.setEndpoint(dto.getEndpoint());
            config.setDefaultModel(dto.getDefaultModel());
            config.setEnabled(dto.getEnabled());
            config.setIsDefault(dto.getIsDefault());
            config.setTimeout(dto.getTimeout());
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());

            configMapper.insert(config);

            // 如果设为默认，清除其他默认
            if (Boolean.TRUE.equals(dto.getIsDefault())) {
                clearOtherDefault(config.getId());
            }
        }

        log.info("保存 AI 配置成功, provider={}", dto.getProvider());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        SysAiConfig config = configMapper.selectOneById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        configMapper.deleteById(id);
        log.info("删除 AI 配置成功, id={}, provider={}", id, config.getProvider());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultProvider(Long id) {
        SysAiConfig config = configMapper.selectOneById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        // 清除其他默认
        clearOtherDefault(id);

        // 设置为默认
        config.setIsDefault(true);
        config.setUpdateTime(LocalDateTime.now());
        configMapper.update(config);

        log.info("设置默认提供商成功, id={}, provider={}", id, config.getProvider());
    }

    @Override
    public String testConfig(Long id) {
        SysAiConfig config = configMapper.selectOneById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        try {
            String response = aiService.chat(config.getProvider(), null, "你好，请用一句话介绍一下自己。");
            return response != null ? response : "测试成功，但未返回内容";
        } catch (Exception e) {
            log.error("测试 AI 配置失败, id={}", id, e);
            throw new BusinessException("测试失败: " + e.getMessage());
        }
    }

    // ==================== Prompt 模板 ====================

    @Override
    public Page<PromptTemplateVO> pagePromptTemplate(PromptTemplateQuery query) {
        Page<SysPromptTemplate> page = promptMapper.paginateByQuery(
                query.getPageNum(),
                query.getPageSize(),
                query
        );
        Page<PromptTemplateVO> voPage = new Page<>();
        voPage.setPageNumber(page.getPageNumber());
        voPage.setPageSize(page.getPageSize());
        voPage.setTotalPage(page.getTotalPage());
        voPage.setTotalRow(page.getTotalRow());
        voPage.setRecords(MapStructUtils.convert(page.getRecords(), PromptTemplateVO.class));
        return voPage;
    }

    @Override
    public PromptTemplateVO getPromptTemplate(Long id) {
        SysPromptTemplate template = promptMapper.selectOneById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        return MapStructUtils.convert(template, PromptTemplateVO.class);
    }

    @Override
    public PromptTemplateVO getPromptTemplateByCode(String code) {
        SysPromptTemplate template = promptMapper.selectByCode(code);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        return MapStructUtils.convert(template, PromptTemplateVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePromptTemplate(PromptTemplateSaveDTO dto) {
        SysPromptTemplate template;

        if (dto.getId() != null) {
            // 更新
            template = promptMapper.selectOneById(dto.getId());
            if (template == null) {
                throw new BusinessException("模板不存在");
            }

            template.setName(dto.getName());
            template.setCategory(dto.getCategory());
            template.setSystemPrompt(dto.getSystemPrompt());
            template.setUserPromptTemplate(dto.getUserPromptTemplate());
            template.setVariables(dto.getVariables());
            template.setDescription(dto.getDescription());
            template.setEnabled(dto.getEnabled());
            template.setSort(dto.getSort());
            template.setUpdateTime(LocalDateTime.now());

            promptMapper.update(template);
        } else {
            // 新增
            // 检查代码是否已存在
            SysPromptTemplate existing = promptMapper.selectByCode(dto.getCode());
            if (existing != null) {
                throw new BusinessException("模板代码已存在");
            }

            template = new SysPromptTemplate();
            template.setName(dto.getName());
            template.setCode(dto.getCode());
            template.setCategory(dto.getCategory());
            template.setSystemPrompt(dto.getSystemPrompt());
            template.setUserPromptTemplate(dto.getUserPromptTemplate());
            template.setVariables(dto.getVariables());
            template.setDescription(dto.getDescription());
            template.setEnabled(dto.getEnabled());
            template.setSort(dto.getSort() != null ? dto.getSort() : 0);
            template.setCreateTime(LocalDateTime.now());
            template.setUpdateTime(LocalDateTime.now());

            promptMapper.insert(template);
        }

        log.info("保存 Prompt 模板成功, code={}", dto.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePromptTemplate(Long id) {
        SysPromptTemplate template = promptMapper.selectOneById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }
        promptMapper.deleteById(id);
        log.info("删除 Prompt 模板成功, id={}, code={}", id, template.getCode());
    }

    @Override
    public List<String> getPromptCategories() {
        List<SysPromptTemplate> all = promptMapper.selectAll();
        return all.stream()
                .map(SysPromptTemplate::getCategory)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public String testPromptTemplate(Long id, Map<String, String> variables) {
        SysPromptTemplate template = promptMapper.selectOneById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        String systemPrompt = template.getSystemPrompt();
        String userMessage = template.getUserPromptTemplate();

        // 替换变量
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                if (systemPrompt != null) {
                    systemPrompt = systemPrompt.replace(placeholder, entry.getValue());
                }
                if (userMessage != null) {
                    userMessage = userMessage.replace(placeholder, entry.getValue());
                }
            }
        }

        // 如果没有用户消息模板，使用默认测试消息
        if (StringUtils.isBlank(userMessage)) {
            userMessage = "请测试一下。";
        }

        try {
            String response = aiService.chat(systemPrompt, userMessage);
            return response != null ? response : "测试成功，但未返回内容";
        } catch (Exception e) {
            log.error("测试 Prompt 模板失败, id={}", id, e);
            throw new BusinessException("测试失败: " + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    private AiConfigVO convertToVO(SysAiConfig config) {
        AiConfigVO vo = new AiConfigVO();
        vo.setId(config.getId());
        vo.setProvider(config.getProvider());
        vo.setProviderName(getProviderName(config.getProvider()));
        vo.setApiKey(maskApiKey(config.getApiKey()));
        vo.setEndpoint(config.getEndpoint());
        vo.setDefaultModel(config.getDefaultModel());
        vo.setEnabled(config.getEnabled());
        vo.setIsDefault(config.getIsDefault());
        vo.setTimeout(config.getTimeout());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }

    private String getProviderName(String code) {
        return code;
    }

    private String maskApiKey(String apiKey) {
        if (StringUtils.isBlank(apiKey) || apiKey.length() < 8) {
            return "******";
        }
        return apiKey.substring(0, 4) + "******" + apiKey.substring(apiKey.length() - 4);
    }

    private void clearOtherDefault(Long excludeId) {
        List<SysAiConfig> allConfigs = configMapper.selectAll();
        for (SysAiConfig c : allConfigs) {
            if (!c.getId().equals(excludeId) && Boolean.TRUE.equals(c.getIsDefault())) {
                c.setIsDefault(false);
                c.setUpdateTime(LocalDateTime.now());
                configMapper.update(c);
            }
        }
    }
}
