package com.glowxq.triflow.base.ai.mapper;

import com.glowxq.triflow.base.ai.entity.SysPromptTemplate;
import com.glowxq.triflow.base.ai.pojo.query.PromptTemplateQuery;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.commons.lang3.StringUtils;

import static com.mybatisflex.core.query.QueryMethods.noCondition;

/**
 * Prompt 模板 Mapper
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Mapper
public interface SysPromptTemplateMapper extends BaseMapper<SysPromptTemplate> {

    /**
     * 根据查询条件分页查询
     */
    default Page<SysPromptTemplate> paginateByQuery(int pageNum, int pageSize, PromptTemplateQuery query) {
        QueryWrapper wrapper = buildQueryWrapper(query);
        return paginate(Page.of(pageNum, pageSize), wrapper);
    }

    /**
     * 构建查询条件
     */
    default QueryWrapper buildQueryWrapper(PromptTemplateQuery query) {
        return QueryWrapper.create()
                .where(SysPromptTemplate::getName).like(query.getName(), query.getName() != null && !query.getName().isBlank())
                .and(SysPromptTemplate::getCode).like(query.getCode(), query.getCode() != null && !query.getCode().isBlank())
                .and(SysPromptTemplate::getCategory).eq(query.getCategory(), query.getCategory() != null && !query.getCategory().isBlank())
                .and(SysPromptTemplate::getEnabled).eq(query.getEnabled(), query.getEnabled() != null)
                .orderBy(SysPromptTemplate::getSort).asc()
                .orderBy(SysPromptTemplate::getId).desc();
    }

    /**
     * 根据模板代码查询
     *
     * @param code 模板代码
     * @return 模板记录
     */
    default SysPromptTemplate selectByCode(String code) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysPromptTemplate.class)
                                      .eq(SysPromptTemplate::getCode, code, StringUtils.isNotBlank(code));
        return selectOneByQuery(qw);
    }
}
