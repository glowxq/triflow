package com.glowxq.triflow.base.notify.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.notify.entity.SysNotifySubscribe;
import com.glowxq.triflow.base.notify.entity.SysNotifyTemplate;
import com.glowxq.triflow.base.notify.enums.NotifyChannelEnum;
import com.glowxq.triflow.base.notify.enums.NotifySubscribeStatusEnum;
import com.glowxq.triflow.base.notify.mapper.SysNotifySubscribeMapper;
import com.glowxq.triflow.base.notify.mapper.SysNotifyTemplateMapper;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeItemDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeQueryDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeSubmitDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeUpdateDTO;
import com.glowxq.triflow.base.notify.pojo.vo.NotifySubscribeVO;
import com.glowxq.triflow.base.notify.service.NotifySubscribeService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息订阅服务实现
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifySubscribeServiceImpl implements NotifySubscribeService {

    private static final int ENABLED_STATUS = 1;

    private final SysNotifyTemplateMapper templateMapper;
    private final SysNotifySubscribeMapper subscribeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long userId, NotifySubscribeSubmitDTO dto) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        NotifyChannelEnum channelEnum = NotifyChannelEnum.of(dto.getChannel());
        if (channelEnum == null) {
            throw new BusinessException("通知渠道不支持");
        }
        if (CollectionUtils.isEmpty(dto.getItems())) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (NotifySubscribeItemDTO item : dto.getItems()) {
            handleSubscribeItem(userId, channelEnum.getCode(), item, now);
        }
    }

    @Override
    public List<NotifySubscribeVO> listByUser(Long userId, String channel) {
        if (userId == null) {
            return List.of();
        }
        NotifyChannelEnum channelEnum = NotifyChannelEnum.of(channel);
        if (channelEnum == null) {
            throw new BusinessException("通知渠道不支持");
        }
        List<SysNotifySubscribe> list = subscribeMapper.selectByUser(userId, channelEnum.getCode());
        return MapStructUtils.convert(list, NotifySubscribeVO.class);
    }

    @Override
    public List<NotifySubscribeVO> list(NotifySubscribeQueryDTO query) {
        if (query.getChannel() != null && NotifyChannelEnum.of(query.getChannel()) == null) {
            throw new BusinessException("通知渠道不支持");
        }
        List<SysNotifySubscribe> list = subscribeMapper.selectByQuery(query);
        return MapStructUtils.convert(list, NotifySubscribeVO.class);
    }

    @Override
    public Page<NotifySubscribeVO> page(NotifySubscribeQueryDTO query) {
        if (query.getChannel() != null && NotifyChannelEnum.of(query.getChannel()) == null) {
            throw new BusinessException("通知渠道不支持");
        }
        QueryWrapper qw = subscribeMapper.buildQueryWrapper(query);
        Page<NotifySubscribeVO> page = query.buildPage();
        return subscribeMapper.paginateAs(page, qw, NotifySubscribeVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(NotifySubscribeUpdateDTO dto) {
        SysNotifySubscribe existing = subscribeMapper.selectOneById(dto.getId());
        if (existing == null) {
            return false;
        }
        NotifySubscribeStatusEnum statusEnum = NotifySubscribeStatusEnum.of(dto.getSubscribeStatus());
        if (statusEnum == null) {
            throw new BusinessException("订阅状态不支持");
        }
        SysNotifySubscribe update = MapStructUtils.convert(dto, SysNotifySubscribe.class);
        update.setSubscribeTime(LocalDateTime.now());
        return subscribeMapper.update(update) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return subscribeMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return subscribeMapper.deleteBatchByIds(ids) > 0;
    }

    private void handleSubscribeItem(Long userId, String channel, NotifySubscribeItemDTO item, LocalDateTime now) {
        NotifySubscribeStatusEnum statusEnum = NotifySubscribeStatusEnum.of(item.getSubscribeStatus());
        if (statusEnum == null) {
            throw new BusinessException("订阅状态不支持");
        }

        SysNotifyTemplate template = templateMapper.selectByTemplateIdAndChannel(item.getTemplateId(), channel);
        if (template == null || template.getStatus() == null || template.getStatus() != ENABLED_STATUS) {
            return;
        }

        SysNotifySubscribe existing = subscribeMapper.selectByUserAndTemplate(userId, template.getTemplateId(), channel);
        if (existing == null) {
            SysNotifySubscribe subscribe = new SysNotifySubscribe();
            subscribe.setUserId(userId);
            subscribe.setTemplateId(template.getTemplateId());
            subscribe.setTemplateKey(template.getTemplateKey());
            subscribe.setChannel(channel);
            subscribe.setSubscribeStatus(statusEnum.getCode());
            subscribe.setSubscribeTime(now);
            subscribeMapper.insert(subscribe);
        }
        else {
            SysNotifySubscribe update = new SysNotifySubscribe();
            update.setId(existing.getId());
            update.setSubscribeStatus(statusEnum.getCode());
            update.setSubscribeTime(now);
            update.setTemplateKey(template.getTemplateKey());
            subscribeMapper.update(update);
        }
    }
}
