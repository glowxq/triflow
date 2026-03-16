package com.glowxq.triflow.base.system.mapper;

import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.system.entity.SysRole;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色数据访问层
 * <p>
 * 提供角色的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色编码查询角色
     *
     * @param code 角色编码
     * @return 角色信息
     */
    default SysRole selectByCode(String code) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysRole.class)
                                      .eq(SysRole::getRoleKey, code);
        return selectOneByQuery(qw);
    }

    /**
     * 查询未删除的角色列表
     *
     * @return 角色列表
     */
    default List<SysRole> selectNotDeleted() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysRole.class)
                                      .eq(SysRole::getDeleted, BooleanEnum.NO.getValue());
        return selectListByQuery(qw);
    }

    /**
     * 查询所有启用的角色
     *
     * @return 角色列表
     */
    default List<SysRole> selectAllEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysRole.class)
                                      .eq(SysRole::getStatus, 1)
                                      .orderBy(SysRole::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 检查角色编码是否存在
     *
     * @param code 角色编码
     * @param excludeId 排除的角色ID
     * @return true 表示存在
     */
    default boolean existsByCode(String code, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysRole.class)
                                      .eq(SysRole::getRoleKey, code)
                                      .ne(SysRole::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 根据查询条件查询角色列表
     *
     * @param query 查询条件
     * @return 角色列表
     */
    default List<SysRole> selectByQuery(SysRoleQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据查询条件分页查询角色
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    default Page<SysRole> paginateByQuery(int pageNum, int pageSize, SysRoleQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(SysRoleQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysRole.class)
                           .like(SysRole::getRoleKey, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysRole::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(SysRole::getSort, true);
    }

}
