package com.glowxq.triflow.base.notify.service;

import com.glowxq.triflow.base.notify.entity.SysNotifyTemplate;
import com.glowxq.triflow.base.notify.pojo.dto.NotifyTemplateCreateDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifyTemplateQueryDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifyTemplateUpdateDTO;
import com.glowxq.triflow.base.notify.pojo.vo.NotifyTemplateVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 消息通知模板服务
 *
 * @author glowxq
 * @since 2025-01-27
 */
public interface NotifyTemplateService {

    Long create(NotifyTemplateCreateDTO dto);

    boolean update(NotifyTemplateUpdateDTO dto);

    NotifyTemplateVO getById(Long id);

    SysNotifyTemplate getByTemplateId(String templateId, String channel);

    boolean deleteById(Long id);

    boolean deleteByIds(List<Long> ids);

    List<NotifyTemplateVO> list(NotifyTemplateQueryDTO query);

    Page<NotifyTemplateVO> page(NotifyTemplateQueryDTO query);

    List<NotifyTemplateVO> listEnabledByChannel(String channel);

}
