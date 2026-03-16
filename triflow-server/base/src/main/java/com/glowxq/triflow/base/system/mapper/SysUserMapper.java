package com.glowxq.triflow.base.system.mapper;

import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.pojo.dto.SysUserQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问层
 * <p>
 * 提供用户的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    default SysUser selectByUsername(String username) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getUsername, username);
        return selectOneByQuery(qw);
    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    default SysUser selectByPhone(String phone) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getPhone, phone);
        return selectOneByQuery(qw);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    default SysUser selectByEmail(String email) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getEmail, email);
        return selectOneByQuery(qw);
    }

    /**
     * 统计未删除用户总数
     *
     * @return 用户总数
     */
    default Long countActiveUsers() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getDeleted, BooleanEnum.NO.getValue());
        return selectCountByQuery(qw);
    }

    /**
     * 统计指定时间范围内的新增用户数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 新增用户数
     */
    default Long countActiveUsersBetween(LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getDeleted, BooleanEnum.NO.getValue())
                                      .between(SysUser::getCreateTime, startTime, endTime);
        return selectCountByQuery(qw);
    }

    /**
     * 根据部门ID查询用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    default List<SysUser> selectByDeptId(Long deptId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getDeptId, deptId, deptId != null)
                                      .orderBy(SysUser::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据关键词模糊查询用户（仅按用户名）
     *
     * @param keyword 关键词
     * @return 用户列表
     */
    default List<SysUser> selectByKeyword(String keyword) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .like(SysUser::getUsername, keyword, StringUtils.isNotBlank(keyword))
                                      .orderBy(SysUser::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return true 表示存在
     */
    default boolean existsByUsername(String username, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getUsername, username)
                                      .ne(SysUser::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @param excludeId 排除的用户ID
     * @return true 表示存在
     */
    default boolean existsByPhone(String phone, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUser.class)
                                      .eq(SysUser::getPhone, phone)
                                      .ne(SysUser::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    /**
     * 根据查询条件查询用户列表
     *
     * @param query 查询条件
     * @return 用户列表
     */
    default List<SysUser> selectByQuery(SysUserQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据查询条件分页查询用户
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     * @return 分页结果
     */
    default Page<SysUser> paginateByQuery(int pageNum, int pageSize, SysUserQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return QueryWrapper
     */
    default QueryWrapper buildQueryWrapper(SysUserQueryDTO query) {
        return QueryWrapper.create()
                           .from(SysUser.class)
                           .like(SysUser::getUsername, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(SysUser::getStatus, query.getStatus(), query.getStatus() != null)
                           .eq(SysUser::getDeptId, query.getDeptId(), query.getDeptId() != null)
                           .eq(SysUser::getDataScope, query.getDataScope(), StringUtils.isNotBlank(query.getDataScope()))
                           .ge(SysUser::getCreateTime, query.getStartTime(), query.getStartTime() != null)
                           .le(SysUser::getCreateTime, query.getEndTime(), query.getEndTime() != null)
                           .orderBy(SysUser::getCreateTime, false);
    }

}
