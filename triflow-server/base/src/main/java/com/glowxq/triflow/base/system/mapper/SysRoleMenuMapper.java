package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysRoleMenu;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色菜单关联数据访问层
 * <p>
 * 提供角色菜单关联的数据库操作方法。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    default List<Long> selectMenuIdsByRoleId(Long roleId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select(SysRoleMenu::getMenuId)
                                      .from(SysRoleMenu.class)
                                      .eq(SysRoleMenu::getRoleId, roleId);
        return selectListByQueryAs(qw, Long.class);
    }

    /**
     * 根据多个角色ID查询菜单ID列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单ID列表
     */
    default List<Long> selectMenuIdsByRoleIds(List<Long> roleIds) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select(SysRoleMenu::getMenuId)
                                      .from(SysRoleMenu.class)
                                      .in(SysRoleMenu::getRoleId, roleIds);
        return selectListByQueryAs(qw, Long.class);
    }

    /**
     * 删除角色的所有菜单关联
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    default int deleteByRoleId(Long roleId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysRoleMenu.class)
                                      .eq(SysRoleMenu::getRoleId, roleId);
        return deleteByQuery(qw);
    }

    /**
     * 删除菜单的所有角色关联
     *
     * @param menuId 菜单ID
     * @return 删除数量
     */
    default int deleteByMenuId(Long menuId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysRoleMenu.class)
                                      .eq(SysRoleMenu::getMenuId, menuId);
        return deleteByQuery(qw);
    }

    /**
     * 批量插入角色菜单关联
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    default void batchInsert(Long roleId, List<Long> menuIds) {
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            insert(roleMenu);
        }
    }

}
