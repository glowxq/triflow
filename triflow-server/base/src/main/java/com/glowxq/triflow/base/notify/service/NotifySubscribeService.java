package com.glowxq.triflow.base.notify.service;

import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeQueryDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeSubmitDTO;
import com.glowxq.triflow.base.notify.pojo.dto.NotifySubscribeUpdateDTO;
import com.glowxq.triflow.base.notify.pojo.vo.NotifySubscribeVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 消息订阅服务
 *
 * @author glowxq
 * @since 2025-01-27
 */
public interface NotifySubscribeService {

    void submit(Long userId, NotifySubscribeSubmitDTO dto);

    List<NotifySubscribeVO> listByUser(Long userId, String channel);

    List<NotifySubscribeVO> list(NotifySubscribeQueryDTO query);

    Page<NotifySubscribeVO> page(NotifySubscribeQueryDTO query);

    boolean updateStatus(NotifySubscribeUpdateDTO dto);

    boolean deleteById(Long id);

    boolean deleteByIds(List<Long> ids);
}
