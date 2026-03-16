package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.entity.SysMenu;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuTreeVO;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuVO;

import java.util.List;

/**
 * 菜单服务接口
 * <p>
 * 定义菜单业务逻辑的契约。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
public interface SysMenuService {

    /**
     * 创建菜单
     *
     * @param dto 创建请求参数
     * @return 创建后的菜单ID
     */
    Long create(SysMenuCreateDTO dto);

    /**
     * 更新菜单
     *
     * @param dto 更新请求参数
     * @return 是否更新成功
     */
    boolean update(SysMenuUpdateDTO dto);

    /**
     * 根据ID获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单 VO
     */
    SysMenuVO getById(Long id);

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 根据条件查询菜单列表
     *
     * @param query 查询条件
     * @return 菜单列表
     */
    List<SysMenuVO> list(SysMenuQueryDTO query);

    /**
     * 获取菜单树形结构
     *
     * @return 菜单树
     */
    List<SysMenuTreeVO> getMenuTree();

    /**
     * 根据用户ID获取菜单树（用于前端渲染）
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<SysMenuTreeVO> getUserMenuTree(Long userId);

    /**
     * 根据用户ID获取菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getUserMenus(Long userId);

    /**
     * 获取所有权限标识（用于超级管理员）
     *
     * @return 权限标识列表
     */
    List<String> getAllPermissions();

    /**
     * 根据菜单ID列表获取权限标识
     *
     * @param menuIds 菜单ID列表
     * @return 权限标识列表
     */
    List<String> getPermissionsByMenuIds(List<Long> menuIds);

}
