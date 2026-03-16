package com.glowxq.triflow.base.wechat.service.impl;

import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.wechat.entity.SysWechatTabbar;
import com.glowxq.triflow.base.wechat.mapper.SysWechatTabbarMapper;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarCreateDTO;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarQueryDTO;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarUpdateDTO;
import com.glowxq.triflow.base.wechat.pojo.vo.WechatTabbarVO;
import com.glowxq.triflow.base.wechat.service.WechatTabbarService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 微信小程序底部菜单服务实现
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Service
@RequiredArgsConstructor
public class WechatTabbarServiceImpl implements WechatTabbarService {

    private static final int DEFAULT_STATUS = 1;
    private static final int DEFAULT_SORT = 0;

    private final SysWechatTabbarMapper tabbarMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WechatTabbarCreateDTO dto) {
        SysWechatTabbar entity = MapStructUtils.convert(dto, SysWechatTabbar.class);
        if (entity.getStatus() == null) {
            entity.setStatus(DEFAULT_STATUS);
        }
        if (entity.getSort() == null) {
            entity.setSort(DEFAULT_SORT);
        }
        tabbarMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(WechatTabbarUpdateDTO dto) {
        SysWechatTabbar existing = tabbarMapper.selectOneById(dto.getId());
        if (existing == null) {
            return false;
        }
        SysWechatTabbar entity = MapStructUtils.convert(dto, SysWechatTabbar.class);
        return tabbarMapper.update(entity) > 0;
    }

    @Override
    public WechatTabbarVO getById(Long id) {
        SysWechatTabbar entity = tabbarMapper.selectOneById(id);
        return MapStructUtils.convert(entity, WechatTabbarVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        return tabbarMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return tabbarMapper.deleteBatchByIds(ids) > 0;
    }

    @Override
    public List<WechatTabbarVO> list(WechatTabbarQueryDTO query) {
        List<SysWechatTabbar> list = tabbarMapper.selectByQuery(query);
        return MapStructUtils.convert(list, WechatTabbarVO.class);
    }

    @Override
    public Page<WechatTabbarVO> page(WechatTabbarQueryDTO query) {
        Page<SysWechatTabbar> page = tabbarMapper.paginateByQuery(query.getPageNum(), query.getPageSize(), query);
        Page<WechatTabbarVO> voPage = new Page<>();
        voPage.setPageNumber(page.getPageNumber());
        voPage.setPageSize(page.getPageSize());
        voPage.setTotalPage(page.getTotalPage());
        voPage.setTotalRow(page.getTotalRow());
        voPage.setRecords(MapStructUtils.convert(page.getRecords(), WechatTabbarVO.class));
        return voPage;
    }

    @Override
    public List<WechatTabbarVO> listEnabled() {
        List<SysWechatTabbar> list = tabbarMapper.selectEnabled();
        return MapStructUtils.convert(list, WechatTabbarVO.class);
    }
}
