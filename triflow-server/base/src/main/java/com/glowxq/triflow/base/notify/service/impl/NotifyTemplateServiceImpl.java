package com.glowxq.triflow.base.notify.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.notify.entity.SysNotifyTemplate;
import com.glowxq.triflow.base.notify.enums.NotifyChannelEnum;
import com.glowxq.triflow.base.notify.mapper.SysNotifyTemplateMapper;
import com.glowxq.triflow.base.notify.pojo.dto.NotifyTemplateCreateDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifyTemplateQueryDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifyTemplateUpdateDTO;
import com.glowxq.triflow.base.notify.pojo.vo.NotifyTemplateVO;
import com.glowxq.triflow.base.notify.service.NotifyTemplateService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息通知模板服务实现
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyTemplateServiceImpl implements NotifyTemplateService {

    private static final int DEFAULT_STATUS = 1;
    private static final int DEFAULT_SORT = 0;

    private final SysNotifyTemplateMapper templateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(NotifyTemplateCreateDTO dto) {
        validateChannel(dto.getChannel());
        if (templateMapper.existsByTemplateKey(dto.getTemplateKey(), null)) {
            throw new BusinessException("模板标识已存在");
        }
        if (templateMapper.existsByTemplateId(dto.getTemplateId(), dto.getChannel(), null)) {
            throw new BusinessException("模板ID已存在");
        }

        SysNotifyTemplate entity = MapStructUtils.convert(dto, SysNotifyTemplate.class);
        if (entity.getStatus() == null) {
            entity.setStatus(DEFAULT_STATUS);
        }
        if (entity.getSort() == null) {
            entity.setSort(DEFAULT_SORT);
        }

        templateMapper.insert(entity);
        log.info("创建通知模板成功, id={}, key={}", entity.getId(), entity.getTemplateKey());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(NotifyTemplateUpdateDTO dto) {
        SysNotifyTemplate existing = templateMapper.selectOneById(dto.getId());
        if (existing == null) {
            return false;
        }

        String channel = StringUtils.isNotBlank(dto.getChannel()) ? dto.getChannel() : existing.getChannel();
        validateChannel(channel);

        if (StringUtils.isNotBlank(dto.getTemplateKey())
                && templateMapper.existsByTemplateKey(dto.getTemplateKey(), dto.getId())) {
            throw new BusinessException("模板标识已存在");
        }
        if (StringUtils.isNotBlank(dto.getTemplateId())
                && templateMapper.existsByTemplateId(dto.getTemplateId(), channel, dto.getId())) {
            throw new BusinessException("模板ID已存在");
        }

        SysNotifyTemplate entity = MapStructUtils.convert(dto, SysNotifyTemplate.class);
        return templateMapper.update(entity) > 0;
    }

    @Override
    public NotifyTemplateVO getById(Long id) {
        SysNotifyTemplate entity = templateMapper.selectOneById(id);
        if (entity == null) {
            return null;
        }
        return MapStructUtils.convert(entity, NotifyTemplateVO.class);
    }

    @Override
    public SysNotifyTemplate getByTemplateId(String templateId, String channel) {
        return templateMapper.selectByTemplateIdAndChannel(templateId, channel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return templateMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return templateMapper.deleteBatchByIds(ids) > 0;
    }

    @Override
    public List<NotifyTemplateVO> list(NotifyTemplateQueryDTO query) {
        List<SysNotifyTemplate> list = templateMapper.selectByQuery(query);
        return MapStructUtils.convert(list, NotifyTemplateVO.class);
    }

    @Override
    public Page<NotifyTemplateVO> page(NotifyTemplateQueryDTO query) {
        QueryWrapper qw = templateMapper.buildQueryWrapper(query);
        Page<NotifyTemplateVO> page = query.buildPage();
        return templateMapper.paginateAs(page, qw, NotifyTemplateVO.class);
    }

    @Override
    public List<NotifyTemplateVO> listEnabledByChannel(String channel) {
        validateChannel(channel);
        List<SysNotifyTemplate> list = templateMapper.selectEnabledByChannel(channel);
        return MapStructUtils.convert(list, NotifyTemplateVO.class);
    }

    private void validateChannel(String channel) {
        if (NotifyChannelEnum.of(channel) == null) {
            throw new BusinessException("通知渠道不支持");
        }
    }
}
