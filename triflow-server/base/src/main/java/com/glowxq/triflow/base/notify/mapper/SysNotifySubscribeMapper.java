package com.glowxq.triflow.base.notify.mapper;

import com.glowxq.triflow.base.notify.entity.SysNotifySubscribe;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 消息订阅 Mapper
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Mapper
public interface SysNotifySubscribeMapper extends BaseMapper<SysNotifySubscribe> {

    /**
     * 根据用户和模板查询订阅记录
     */
    default SysNotifySubscribe selectByUserAndTemplate(Long userId, String templateId, String channel) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysNotifySubscribe.class)
                                      .eq(SysNotifySubscribe::getUserId, userId, userId != null)
                                      .eq(SysNotifySubscribe::getTemplateId, templateId, StringUtils.isNotBlank(templateId))
                                      .eq(SysNotifySubscribe::getChannel, channel, StringUtils.isNotBlank(channel));
        return selectOneByQuery(qw);
    }

    /**
     * 查询用户订阅列表
     */
    default List<SysNotifySubscribe> selectByUser(Long userId, String channel) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysNotifySubscribe.class)
                                      .eq(SysNotifySubscribe::getUserId, userId, userId != null)
                                      .eq(SysNotifySubscribe::getChannel, channel, StringUtils.isNotBlank(channel))
                                      .orderBy(SysNotifySubscribe::getSubscribeTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据查询条件查询订阅列表
     */
    default List<SysNotifySubscribe> selectByQuery(NotifySubscribeQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据查询条件分页查询订阅列表
     */
    default Page<SysNotifySubscribe> paginateByQuery(int pageNum, int pageSize, NotifySubscribeQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     */
    default QueryWrapper buildQueryWrapper(NotifySubscribeQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysNotifySubscribe.class)
                           .and(wrapper -> wrapper
                                   .like(SysNotifySubscribe::getTemplateId, query.getKeyword())
                                   .or(SysNotifySubscribe::getTemplateKey).like(query.getKeyword()),
                                   StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysNotifySubscribe::getUserId, query.getUserId(), query.getUserId() != null)
                           .eq(SysNotifySubscribe::getChannel, query.getChannel(), StringUtils.isNotBlank(query.getChannel()))
                           .eq(SysNotifySubscribe::getSubscribeStatus, query.getSubscribeStatus(), StringUtils.isNotBlank(query.getSubscribeStatus()))
                           .orderBy(SysNotifySubscribe::getSubscribeTime, false);
    }

}
