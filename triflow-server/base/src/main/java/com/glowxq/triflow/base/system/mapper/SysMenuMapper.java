package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysMenu;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单数据访问层
 * <p>
 * 提供菜单的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据父级ID查询子菜单
     *
     * @param parentId 父级菜单ID
     * @return 菜单列表
     */
    default List<SysMenu> selectByParentId(Long parentId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysMenu.class)
                                      .eq(SysMenu::getParentId, parentId)
                                      .orderBy(SysMenu::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 查询所有启用的菜单
     *
     * @return 菜单列表
     */
    default List<SysMenu> selectAllEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysMenu.class)
                                      .eq(SysMenu::getStatus, 1)
                                      .orderBy(SysMenu::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 根据菜单类型查询
     *
     * @param menuType 菜单类型 (M:目录, C:菜单, F:按钮)
     * @return 菜单列表
     */
    default List<SysMenu> selectByMenuType(String menuType) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysMenu.class)
                                      .eq(SysMenu::getMenuType, menuType)
                                      .eq(SysMenu::getStatus, 1)
                                      .orderBy(SysMenu::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 根据名称查询菜单
     *
     * @param name 菜单名称（路由name）
     * @return 菜单信息
     */
    default SysMenu selectByName(String name) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysMenu.class)
                                      .eq(SysMenu::getName, name);
        return selectOneByQuery(qw);
    }

    /**
     * 检查是否存在子菜单
     *
     * @param parentId 父级菜单ID
     * @return true 表示存在
     */
    default boolean existsChildren(Long parentId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysMenu.class)
                                      .eq(SysMenu::getParentId, parentId);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 根据查询条件查询菜单列表
     *
     * @param query 查询条件
     * @return 菜单列表
     */
    default List<SysMenu> selectByQuery(SysMenuQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(SysMenuQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysMenu.class)
                           .like(SysMenu::getMenuName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysMenu::getMenuType, query.getMenuType(), StringUtils.isNotBlank(query.getMenuType()))
                           .eq(SysMenu::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(SysMenu::getSort, true);
    }

}
