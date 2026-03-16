package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.entity.SysRole;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysRoleVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 角色服务接口
 * <p>
 * 定义角色业务逻辑的契约。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
public interface SysRoleService {

    /**
     * 创建角色
     *
     * @param dto 创建请求参数
     * @return 创建后的角色ID
     */
    Long create(SysRoleCreateDTO dto);

    /**
     * 更新角色
     *
     * @param dto 更新请求参数
     * @return 是否更新成功
     */
    boolean update(SysRoleUpdateDTO dto);

    /**
     * 根据ID获取角色详情
     *
     * @param id 角色ID
     * @return 角色 VO
     */
    SysRoleVO getById(Long id);

    /**
     * 根据ID删除角色
     *
     * @param id 角色ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids);

    /**
     * 根据条件查询角色列表
     *
     * @param query 查询条件
     * @return 角色列表
     */
    List<SysRoleVO> list(SysRoleQueryDTO query);

    /**
     * 分页查询角色列表
     *
     * @param query 查询条件（包含分页参数）
     * @return 分页结果
     */
    Page<SysRoleVO> page(SysRoleQueryDTO query);

    /**
     * 获取所有启用的角色列表
     *
     * @return 角色列表
     */
    List<SysRoleVO> listAllEnabled();

    /**
     * 根据角色编码查询
     *
     * @param code 角色编码
     * @return 角色实体
     */
    SysRole getByCode(String code);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 分配角色菜单权限
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取角色的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

}
