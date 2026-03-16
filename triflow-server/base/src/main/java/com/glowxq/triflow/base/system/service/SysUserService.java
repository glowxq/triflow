package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.pojo.dto.SysUserCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysUserQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysUserUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserDetailVO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口
 * <p>
 * 定义用户业务逻辑的契约，Service 层只负责业务逻辑编排，
 * 不包含任何 SQL 或 QueryWrapper 逻辑（这些封装在 Mapper 层）。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
public interface SysUserService {

    /**
     * 创建用户
     *
     * @param dto 创建请求参数
     * @return 创建后的用户ID
     */
    Long create(SysUserCreateDTO dto);

    /**
     * 更新用户
     *
     * @param dto 更新请求参数
     * @return 是否更新成功
     */
    boolean update(SysUserUpdateDTO dto);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情 VO
     */
    SysUserDetailVO getById(Long id);

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids);

    /**
     * 根据条件查询用户列表
     *
     * @param query 查询条件
     * @return 用户列表
     */
    List<SysUserVO> list(SysUserQueryDTO query);

    /**
     * 分页查询用户列表
     *
     * @param query 查询条件（包含分页参数）
     * @return 分页结果
     */
    Page<SysUserVO> page(SysUserQueryDTO query);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    SysUser getByPhone(String phone);

    /**
     * 获取用户角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    Set<String> getUserRoleCodes(Long userId);

    /**
     * 获取用户权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 更新用户登录信息
     *
     * @param userId  用户ID
     * @param loginIp 登录IP
     */
    void updateLoginInfo(Long userId, String loginIp);

    /**
     * 根据实体更新用户（仅更新非空字段）
     *
     * @param user 用户实体
     * @return 是否成功
     */
    boolean updateById(SysUser user);

    /**
     * 重置用户密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码（明文）
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 分配用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 批量设置数据权限
     *
     * @param userIds   用户ID列表
     * @param dataScope 数据权限范围
     * @return 更新的记录数
     */
    int batchUpdateDataScope(List<Long> userIds, String dataScope);

}
