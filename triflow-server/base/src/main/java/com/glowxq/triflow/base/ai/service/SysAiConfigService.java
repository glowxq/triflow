package com.glowxq.triflow.base.ai.service;

import com.glowxq.triflow.base.ai.pojo.dto.AiConfigSaveDTO;
import com.glowxq.triflow.base.ai.pojo.dto.PromptTemplateSaveDTO;
import com.glowxq.triflow.base.ai.pojo.query.PromptTemplateQuery;
import com.glowxq.triflow.base.ai.pojo.vo.AiConfigVO;
import com.glowxq.triflow.base.ai.pojo.vo.PromptTemplateVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * AI 配置服务接口
 *
 * @author glowxq
 * @since 2025-01-28
 */
public interface SysAiConfigService {

    // ==================== AI 配置 ====================

    /**
     * 获取 AI 配置列表
     */
    List<AiConfigVO> getConfigList();

    /**
     * 获取 AI 配置详情
     */
    AiConfigVO getConfig(Long id);

    /**
     * 保存 AI 配置
     */
    void saveConfig(AiConfigSaveDTO dto);

    /**
     * 删除 AI 配置
     */
    void deleteConfig(Long id);

    /**
     * 设置默认提供商
     */
    void setDefaultProvider(Long id);

    /**
     * 测试 AI 配置
     */
    String testConfig(Long id);

    // ==================== Prompt 模板 ====================

    /**
     * 分页查询 Prompt 模板
     */
    Page<PromptTemplateVO> pagePromptTemplate(PromptTemplateQuery query);

    /**
     * 获取 Prompt 模板详情
     */
    PromptTemplateVO getPromptTemplate(Long id);

    /**
     * 根据代码获取 Prompt 模板
     */
    PromptTemplateVO getPromptTemplateByCode(String code);

    /**
     * 保存 Prompt 模板
     */
    void savePromptTemplate(PromptTemplateSaveDTO dto);

    /**
     * 删除 Prompt 模板
     */
    void deletePromptTemplate(Long id);

    /**
     * 获取 Prompt 分类列表
     */
    List<String> getPromptCategories();

    /**
     * 测试 Prompt 模板
     */
    String testPromptTemplate(Long id, java.util.Map<String, String> variables);
}
