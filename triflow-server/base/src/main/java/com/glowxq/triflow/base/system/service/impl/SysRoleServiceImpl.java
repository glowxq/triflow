package com.glowxq.triflow.base.system.service.impl;

import com.glowxq.common.core.common.event.EventPublisher;
import com.glowxq.common.core.common.event.RolePermissionChangedEvent;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysRole;
import com.glowxq.triflow.base.system.mapper.SysRoleMapper;
import com.glowxq.triflow.base.system.mapper.SysRoleMenuMapper;
import com.glowxq.triflow.base.system.mapper.SysUserRoleMapper;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysRoleVO;
import com.glowxq.triflow.base.system.service.SysRoleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现类
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysRoleCreateDTO dto) {
        // 检查角色编码是否存在
        if (roleMapper.existsByCode(dto.getRoleKey(), null)) {
            throw new BusinessException("角色编码已存在");
        }

        // DTO → Entity 转换
        SysRole role = MapStructUtils.convert(dto, SysRole.class);

        // 设置默认值
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getSort() == null) {
            role.setSort(0);
        }

        // 持久化
        roleMapper.insert(role);

        // 分配菜单权限
        if (CollectionUtils.isNotEmpty(dto.getMenuIds())) {
            roleMenuMapper.batchInsert(role.getId(), dto.getMenuIds());
        }

        log.info("创建角色成功, id={}, roleKey={}", role.getId(), role.getRoleKey());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysRoleUpdateDTO dto) {
        SysRole existingRole = roleMapper.selectOneById(dto.getId());
        if (existingRole == null) {
            log.warn("更新角色失败, 角色不存在, id={}", dto.getId());
            return false;
        }

        // 检查角色编码唯一性
        if (StringUtils.isNotBlank(dto.getRoleKey())
            && roleMapper.existsByCode(dto.getRoleKey(), dto.getId())) {
            throw new BusinessException("角色编码已存在");
        }

        // DTO → Entity 转换
        SysRole role = MapStructUtils.convert(dto, SysRole.class);

        // 执行更新
        int rows = roleMapper.update(role);

        // 更新菜单权限
        if (dto.getMenuIds() != null) {
            roleMenuMapper.deleteByRoleId(dto.getId());
            if (CollectionUtils.isNotEmpty(dto.getMenuIds())) {
                roleMenuMapper.batchInsert(dto.getId(), dto.getMenuIds());
            }
        }

        log.info("更新角色, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    public SysRoleVO getById(Long id) {
        SysRole role = roleMapper.selectOneById(id);
        if (role == null) {
            return null;
        }
        return MapStructUtils.convert(role, SysRoleVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        // 检查是否有用户关联
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(id);
        if (CollectionUtils.isNotEmpty(userIds)) {
            throw new BusinessException("该角色下存在用户，无法删除");
        }

        // 删除角色菜单关联
        roleMenuMapper.deleteByRoleId(id);
        // 删除角色
        int rows = roleMapper.deleteById(id);
        log.info("删除角色, id={}, affected={}", id, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }

        for (Long id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public List<SysRoleVO> list(SysRoleQueryDTO query) {
        List<SysRole> roles = roleMapper.selectByQuery(query);
        return MapStructUtils.convert(roles, SysRoleVO.class);
    }

    @Override
    public Page<SysRoleVO> page(SysRoleQueryDTO query) {
        QueryWrapper qw = roleMapper.buildQueryWrapper(query);
        Page<SysRole> entityPage = roleMapper.paginate(query.buildPage(), qw);
        Page<SysRoleVO> voPage = new Page<>(entityPage.getRecords() != null
                ? MapStructUtils.convert(entityPage.getRecords(), SysRoleVO.class)
                : java.util.List.of(),
                entityPage.getPageNumber(), entityPage.getPageSize(), entityPage.getTotalRow());
        return voPage;
    }

    @Override
    public List<SysRoleVO> listAllEnabled() {
        List<SysRole> roles = roleMapper.selectAllEnabled();
        return MapStructUtils.convert(roles, SysRoleVO.class);
    }

    @Override
    public SysRole getByCode(String code) {
        return roleMapper.selectByCode(code);
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return List.of();
        }
        return roleMapper.selectListByIds(roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 1. 获取受影响的用户ID列表（在权限变更前）
        List<Long> affectedUserIds = userRoleMapper.selectUserIdsByRoleId(roleId);

        // 2. 更新角色菜单权限
        roleMenuMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isNotEmpty(menuIds)) {
            roleMenuMapper.batchInsert(roleId, menuIds);
        }

        // 3. 发布角色权限变更事件，踢出受影响的用户
        if (CollectionUtils.isNotEmpty(affectedUserIds)) {
            log.info("角色 {} 权限变更，受影响用户数: {}", roleId, affectedUserIds.size());
            eventPublisher.publish(new RolePermissionChangedEvent(this, roleId, affectedUserIds));
        }
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

}
