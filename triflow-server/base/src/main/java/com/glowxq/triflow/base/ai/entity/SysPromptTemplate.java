package com.glowxq.triflow.base.ai.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Prompt 模板实体
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Table("sys_prompt_template")
public class SysPromptTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板代码
     */
    private String code;

    /**
     * 模板分类
     */
    private String category;

    /**
     * 系统提示
     */
    private String systemPrompt;

    /**
     * 用户提示模板
     */
    private String userPromptTemplate;

    /**
     * 模板变量 (JSON 数组)
     */
    private String variables;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
