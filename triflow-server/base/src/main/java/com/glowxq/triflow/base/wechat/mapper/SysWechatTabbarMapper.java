package com.glowxq.triflow.base.wechat.mapper;

import com.glowxq.triflow.base.wechat.entity.SysWechatTabbar;
import com.glowxq.triflow.base.wechat.pojo.dto.WechatTabbarQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 微信小程序底部菜单 Mapper
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Mapper
public interface SysWechatTabbarMapper extends BaseMapper<SysWechatTabbar> {

    /**
     * 查询菜单列表
     */
    default List<SysWechatTabbar> selectByQuery(WechatTabbarQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 分页查询菜单列表
     */
    default Page<SysWechatTabbar> paginateByQuery(int pageNum, int pageSize, WechatTabbarQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 查询启用菜单
     */
    default List<SysWechatTabbar> selectEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysWechatTabbar.class)
                                      .eq(SysWechatTabbar::getStatus, 1)
                                      .orderBy(SysWechatTabbar::getSort, true)
                                      .orderBy(SysWechatTabbar::getCreateTime, true);
        return selectListByQuery(qw);
    }

    /**
     * 构建查询条件
     */
    default QueryWrapper buildQueryWrapper(WechatTabbarQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysWechatTabbar.class)
                           .and(wrapper -> wrapper
                                   .like(SysWechatTabbar::getText, query.getKeyword())
                                   .or(SysWechatTabbar::getPagePath).like(query.getKeyword()),
                                   StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysWechatTabbar::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(SysWechatTabbar::getSort, true)
                           .orderBy(SysWechatTabbar::getCreateTime, true);
    }
}
