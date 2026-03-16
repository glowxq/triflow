package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysUserRole;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户角色关联数据访问层
 * <p>
 * 提供用户角色关联的数据库操作方法。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    default List<Long> selectRoleIdsByUserId(Long userId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select(SysUserRole::getRoleId)
                                      .from(SysUserRole.class)
                                      .eq(SysUserRole::getUserId, userId);
        return selectListByQueryAs(qw, Long.class);
    }

    /**
     * 统计指定角色的用户数量
     *
     * @param roleId 角色ID
     * @return 用户数量
     */
    default Long countByRoleId(Long roleId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserRole.class)
                                      .eq(SysUserRole::getRoleId, roleId);
        return selectCountByQuery(qw);
    }

    /**
     * 根据角色ID查询用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    default List<Long> selectUserIdsByRoleId(Long roleId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .select(SysUserRole::getUserId)
                                      .from(SysUserRole.class)
                                      .eq(SysUserRole::getRoleId, roleId);
        return selectListByQueryAs(qw, Long.class);
    }

    /**
     * 删除用户的所有角色关联
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    default int deleteByUserId(Long userId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserRole.class)
                                      .eq(SysUserRole::getUserId, userId);
        return deleteByQuery(qw);
    }

    /**
     * 删除角色的所有用户关联
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    default int deleteByRoleId(Long roleId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserRole.class)
                                      .eq(SysUserRole::getRoleId, roleId);
        return deleteByQuery(qw);
    }

    /**
     * 批量插入用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    default void batchInsert(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            insert(userRole);
        }
    }

}
