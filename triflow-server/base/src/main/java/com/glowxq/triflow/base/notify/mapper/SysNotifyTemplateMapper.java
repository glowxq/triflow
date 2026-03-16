package com.glowxq.triflow.base.notify.mapper;

import com.glowxq.triflow.base.notify.entity.SysNotifyTemplate;
import com.glowxq.triflow.base.notify.pojo.dto.NotifyTemplateQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 消息通知模板 Mapper
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Mapper
public interface SysNotifyTemplateMapper extends BaseMapper<SysNotifyTemplate> {

    /**
     * 判断模板标识是否存在
     */
    default boolean existsByTemplateKey(String templateKey, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysNotifyTemplate.class)
                                      .eq(SysNotifyTemplate::getTemplateKey, templateKey, StringUtils.isNotBlank(templateKey))
                                      .ne(SysNotifyTemplate::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 判断模板ID是否存在
     */
    default boolean existsByTemplateId(String templateId, String channel, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysNotifyTemplate.class)
                                      .eq(SysNotifyTemplate::getTemplateId, templateId, StringUtils.isNotBlank(templateId))
                                      .eq(SysNotifyTemplate::getChannel, channel, StringUtils.isNotBlank(channel))
                                      .ne(SysNotifyTemplate::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 根据模板标识查询模板
     */
    default SysNotifyTemplate selectByTemplateKey(String templateKey) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysNotifyTemplate.class)
                                      .eq(SysNotifyTemplate::getTemplateKey, templateKey, StringUtils.isNotBlank(templateKey));
        return selectOneByQuery(qw);
    }

    /**
     * 根据模板ID和渠道查询模板
     */
    default SysNotifyTemplate selectByTemplateIdAndChannel(String templateId, String channel) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysNotifyTemplate.class)
                                      .eq(SysNotifyTemplate::getTemplateId, templateId, StringUtils.isNotBlank(templateId))
                                      .eq(SysNotifyTemplate::getChannel, channel, StringUtils.isNotBlank(channel));
        return selectOneByQuery(qw);
    }

    /**
     * 查询模板列表
     */
    default List<SysNotifyTemplate> selectByQuery(NotifyTemplateQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 分页查询模板列表
     */
    default Page<SysNotifyTemplate> paginateByQuery(int pageNum, int pageSize, NotifyTemplateQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 查询启用模板
     */
    default List<SysNotifyTemplate> selectEnabledByChannel(String channel) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysNotifyTemplate.class)
                                      .eq(SysNotifyTemplate::getChannel, channel, StringUtils.isNotBlank(channel))
                                      .eq(SysNotifyTemplate::getStatus, 1)
                                      .orderBy(SysNotifyTemplate::getSort, true)
                                      .orderBy(SysNotifyTemplate::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 构建查询条件
     */
    default QueryWrapper buildQueryWrapper(NotifyTemplateQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysNotifyTemplate.class)
                           .like(SysNotifyTemplate::getTemplateName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysNotifyTemplate::getChannel, query.getChannel(), StringUtils.isNotBlank(query.getChannel()))
                           .eq(SysNotifyTemplate::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(SysNotifyTemplate::getSort, true)
                           .orderBy(SysNotifyTemplate::getCreateTime, false);
    }

}
