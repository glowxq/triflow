package com.glowxq.triflow.base.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.glowxq.common.core.common.enums.DataScopeEnum;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysRole;
import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.enums.ConfigKeyEnum;
import com.glowxq.triflow.base.system.mapper.*;
import com.glowxq.triflow.base.system.pojo.dto.SysUserCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysUserQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysUserUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserDetailVO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserVO;
import com.glowxq.triflow.base.system.service.SysConfigService;
import com.glowxq.triflow.base.system.service.SysUserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysUserCreateDTO dto) {
        // 检查用户名是否存在
        if (StringUtils.isNotBlank(dto.getUsername()) && userMapper.existsByUsername(dto.getUsername(), null)) {
            throw new BusinessException("用户名已存在");
        }
        // 检查手机号是否存在
        if (StringUtils.isNotBlank(dto.getPhone()) && userMapper.existsByPhone(dto.getPhone(), null)) {
            throw new BusinessException("手机号已存在");
        }

        // DTO → Entity 转换
        SysUser user = MapStructUtils.convert(dto, SysUser.class);

        // 密码加密
        if (StringUtils.isNotBlank(dto.getPassword())) {
            user.setPassword(BCrypt.hashpw(dto.getPassword()));
        }

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);  // 默认正常
        }
        if (user.getPasswordSet() == null) {
            user.setPasswordSet(1);
        }
        // 设置默认数据权限
        if (StringUtils.isBlank(user.getDataScope())) {
            String defaultDataScope = configService.getValueByKey(
                ConfigKeyEnum.SYS_USER_DEFAULT_DATA_SCOPE.getKey(),
                DataScopeEnum.UserCreate.getCode()
            );
            user.setDataScope(defaultDataScope);
        }

        // 持久化
        userMapper.insert(user);

        // 分配角色
        if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
            userRoleMapper.batchInsert(user.getId(), dto.getRoleIds());
        }

        log.info("创建用户成功, id={}, username={}", user.getId(), user.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysUserUpdateDTO dto) {
        // 检查用户是否存在
        SysUser existingUser = userMapper.selectOneById(dto.getId());
        if (existingUser == null) {
            log.warn("更新用户失败, 用户不存在, id={}", dto.getId());
            return false;
        }

        // 检查用户名唯一性
        if (StringUtils.isNotBlank(dto.getUsername())
            && userMapper.existsByUsername(dto.getUsername(), dto.getId())) {
            throw new BusinessException("用户名已存在");
        }
        // 检查手机号唯一性
        if (StringUtils.isNotBlank(dto.getPhone())
            && userMapper.existsByPhone(dto.getPhone(), dto.getId())) {
            throw new BusinessException("手机号已存在");
        }

        // DTO → Entity 转换
        SysUser user = MapStructUtils.convert(dto, SysUser.class);

        // 执行更新
        int rows = userMapper.update(user);

        // 更新角色
        if (dto.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(dto.getId());
            if (CollectionUtils.isNotEmpty(dto.getRoleIds())) {
                userRoleMapper.batchInsert(dto.getId(), dto.getRoleIds());
            }
        }

        log.info("更新用户, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    public SysUserDetailVO getById(Long id) {
        SysUser user = userMapper.selectOneById(id);
        if (user == null) {
            return null;
        }

        SysUserDetailVO vo = MapStructUtils.convert(user, SysUserDetailVO.class);

        // 获取角色信息
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(id);
        vo.setRoleIds(roleIds);

        if (CollectionUtils.isNotEmpty(roleIds)) {
            List<SysRole> roles = roleMapper.selectListByIds(roleIds);
            vo.setRoleNames(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        // 删除用户角色关联
        userRoleMapper.deleteByUserId(id);
        // 删除用户
        int rows = userMapper.deleteById(id);
        log.info("删除用户, id={}, affected={}", id, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }

        // 删除用户角色关联
        for (Long id : ids) {
            userRoleMapper.deleteByUserId(id);
        }
        // 删除用户
        int rows = userMapper.deleteBatchByIds(ids);
        log.info("批量删除用户, ids={}, affected={}", ids, rows);
        return rows > 0;
    }

    @Override
    public List<SysUserVO> list(SysUserQueryDTO query) {
        List<SysUser> users = userMapper.selectByQuery(query);
        return MapStructUtils.convert(users, SysUserVO.class);
    }

    @Override
    public Page<SysUserVO> page(SysUserQueryDTO query) {
        QueryWrapper qw = userMapper.buildQueryWrapper(query);
        Page<SysUserVO> page = query.buildPage();
        return userMapper.paginateAs(page, qw, SysUserVO.class);
    }

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public SysUser getByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public Set<String> getUserRoleCodes(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return new HashSet<>();
        }

        List<SysRole> roles = roleMapper.selectListByIds(roleIds);
        return roles.stream()
                    .filter(r -> r.getStatus() == 1)  // 只返回启用的角色
                    .map(SysRole::getRoleKey)
                    .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        // 获取用户角色
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return new HashSet<>();
        }

        // 获取角色对应的菜单ID
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(menuIds)) {
            return new HashSet<>();
        }

        // 获取菜单对应的权限标识
        return menuMapper.selectListByIds(menuIds).stream()
                         .filter(m -> StringUtils.isNotBlank(m.getPermission()))
                         .map(m -> m.getPermission())
                         .collect(Collectors.toSet());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginInfo(Long userId, String loginIp) {
        // 简化实现：更新时间已自动更新
        log.debug("记录用户登录信息, userId={}, loginIp={}", userId, loginIp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setPasswordSet(1);
        return userMapper.update(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (CollectionUtils.isNotEmpty(roleIds)) {
            userRoleMapper.batchInsert(userId, roleIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SysUser user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        int rows = userMapper.update(user);
        log.info("更新用户资料, id={}, affected={}", user.getId(), rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateDataScope(List<Long> userIds, String dataScope) {
        if (CollectionUtils.isEmpty(userIds) || StringUtils.isBlank(dataScope)) {
            return 0;
        }

        int totalRows = 0;
        for (Long userId : userIds) {
            SysUser user = new SysUser();
            user.setId(userId);
            user.setDataScope(dataScope);
            totalRows += userMapper.update(user);
        }

        log.info("批量设置数据权限, userIds={}, dataScope={}, affected={}", userIds, dataScope, totalRows);
        return totalRows;
    }

}
