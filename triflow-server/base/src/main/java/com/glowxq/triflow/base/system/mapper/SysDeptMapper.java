package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysDept;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门数据访问层
 * <p>
 * 提供部门的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 根据父级ID查询子部门
     *
     * @param pid 父级部门ID
     * @return 部门列表
     */
    default List<SysDept> selectByPid(Long pid) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysDept.class)
                                      .eq(SysDept::getParentId, pid)
                                      .orderBy(SysDept::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 查询所有启用的部门
     *
     * @return 部门列表
     */
    default List<SysDept> selectAllEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysDept.class)
                                      .eq(SysDept::getStatus, 1)
                                      .orderBy(SysDept::getSort, true);
        return selectListByQuery(qw);
    }

    /**
     * 使用递归 CTE 查询部门及其所有子孙部门的 ID
     * <p>
     * 优势：
     * <ul>
     *   <li>不依赖 ancestors 字段</li>
     *   <li>查询性能稳定，不受树深度影响</li>
     *   <li>标准 SQL，可移植性好</li>
     * </ul>
     * </p>
     *
     * @param deptId 起始部门ID
     * @return 包含自身及所有子孙部门的 ID 列表
     */
    @Select("""
            WITH RECURSIVE dept_tree AS (
                SELECT id FROM sys_dept WHERE id = #{deptId}
                UNION ALL
                SELECT d.id FROM sys_dept d
                INNER JOIN dept_tree dt ON d.parent_id = dt.id
            )
            SELECT id FROM dept_tree
            """)
    List<Long> selectChildDeptIds(@Param("deptId") Long deptId);

    /**
     * 检查是否存在子部门
     *
     * @param pid 父级部门ID
     * @return true 表示存在
     */
    default boolean existsChildren(Long pid) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysDept.class)
                                      .eq(SysDept::getParentId, pid);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 根据查询条件查询部门列表
     *
     * @param query 查询条件
     * @return 部门列表
     */
    default List<SysDept> selectByQuery(SysDeptQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(SysDeptQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysDept.class)
                           .like(SysDept::getDeptName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysDept::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(SysDept::getSort, true);
    }

}
