package com.glowxq.triflow.base.wechat.service;

import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarCreateDTO;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarQueryDTO;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarUpdateDTO;
import com.glowxq.triflow.base.wechat.pojo.vo.WechatTabbarVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 微信小程序底部菜单服务
 *
 * @author glowxq
 * @since 2025-02-01
 */
public interface WechatTabbarService {

    Long create(WechatTabbarCreateDTO dto);

    boolean update(WechatTabbarUpdateDTO dto);

    WechatTabbarVO getById(Long id);

    boolean deleteById(Long id);

    boolean deleteByIds(List<Long> ids);

    List<WechatTabbarVO> list(WechatTabbarQueryDTO query);

    Page<WechatTabbarVO> page(WechatTabbarQueryDTO query);

    List<WechatTabbarVO> listEnabled();
}
