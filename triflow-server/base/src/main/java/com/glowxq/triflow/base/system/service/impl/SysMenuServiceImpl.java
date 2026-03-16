package com.glowxq.triflow.base.system.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysMenu;
import com.glowxq.triflow.base.system.enums.MenuTypeEnum;
import com.glowxq.triflow.base.system.mapper.SysMenuMapper;
import com.glowxq.triflow.base.system.mapper.SysRoleMenuMapper;
import com.glowxq.triflow.base.system.mapper.SysUserRoleMapper;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuTreeVO;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuVO;
import com.glowxq.triflow.base.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysMenuCreateDTO dto) {
        // DTO → Entity 转换
        SysMenu menu = MapStructUtils.convert(dto, SysMenu.class);

        // 设置默认值
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getIsCache() == null) {
            menu.setIsCache(1);
        }
        if (menu.getIsFrame() == null) {
            menu.setIsFrame(0);
        }
        if (menu.getIsAffix() == null) {
            menu.setIsAffix(0);
        }
        if (StringUtils.isBlank(menu.getMenuType())) {
            menu.setMenuType(MenuTypeEnum.MENU.getCode());  // 默认菜单类型
        }

        // 持久化
        menuMapper.insert(menu);

        log.info("创建菜单成功, id={}, name={}", menu.getId(), menu.getName());
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysMenuUpdateDTO dto) {
        SysMenu existingMenu = menuMapper.selectOneById(dto.getId());
        if (existingMenu == null) {
            log.warn("更新菜单失败, 菜单不存在, id={}", dto.getId());
            return false;
        }

        // 不能将父菜单设为自己
        if (dto.getParentId() != null && dto.getParentId().equals(dto.getId())) {
            throw new BusinessException("不能将父菜单设为自己");
        }

        // DTO → Entity 转换
        SysMenu menu = MapStructUtils.convert(dto, SysMenu.class);

        // 执行更新
        int rows = menuMapper.update(menu);
        log.info("更新菜单, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    public SysMenuVO getById(Long id) {
        SysMenu menu = menuMapper.selectOneById(id);
        if (menu == null) {
            return null;
        }
        return MapStructUtils.convert(menu, SysMenuVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        // 检查是否存在子菜单
        if (menuMapper.existsChildren(id)) {
            throw new BusinessException("存在子菜单，无法删除");
        }

        // 删除角色菜单关联
        roleMenuMapper.deleteByMenuId(id);
        // 删除菜单
        int rows = menuMapper.deleteById(id);
        log.info("删除菜单, id={}, affected={}", id, rows);
        return rows > 0;
    }

    @Override
    public List<SysMenuVO> list(SysMenuQueryDTO query) {
        List<SysMenu> menus = menuMapper.selectByQuery(query);
        return MapStructUtils.convert(menus, SysMenuVO.class);
    }

    @Override
    public List<SysMenuTreeVO> getMenuTree() {
        // 获取所有启用的菜单
        List<SysMenu> menus = menuMapper.selectAllEnabled();
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<SysMenuTreeVO> getUserMenuTree(Long userId) {
        List<SysMenu> menus = getUserMenus(userId);
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<SysMenu> getUserMenus(Long userId) {
        // 获取用户角色
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return List.of();
        }

        // 获取角色对应的菜单ID
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(menuIds)) {
            return List.of();
        }

        // 获取菜单详情（只获取启用的菜单和目录，排除按钮）
        return menuMapper.selectListByIds(menuIds).stream()
                         .filter(m -> StatusEnum.ENABLED.getValue().equals(m.getStatus()))
                         .filter(m -> !MenuTypeEnum.BUTTON.getCode().equals(m.getMenuType()))
                         .sorted((a, b) -> {
                             int sortA = a.getSort() != null ? a.getSort() : 0;
                             int sortB = b.getSort() != null ? b.getSort() : 0;
                             return sortA - sortB;
                         })
                         .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllPermissions() {
        List<SysMenu> menus = menuMapper.selectAllEnabled();
        return menus.stream()
                    .map(SysMenu::getPermission)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
    }

    @Override
    public List<String> getPermissionsByMenuIds(List<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return List.of();
        }
        return menuMapper.selectListByIds(menuIds).stream()
                         .map(SysMenu::getPermission)
                         .filter(StringUtils::isNotBlank)
                         .collect(Collectors.toList());
    }

    /**
     * 构建菜单树
     */
    private List<SysMenuTreeVO> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<SysMenuTreeVO> result = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                SysMenuTreeVO vo = convertToTreeVO(menu);
                vo.setChildren(buildMenuTree(menus, menu.getId()));
                result.add(vo);
            }
        }

        return result;
    }

    /**
     * 转换为树形VO
     */
    private SysMenuTreeVO convertToTreeVO(SysMenu menu) {
        SysMenuTreeVO vo = new SysMenuTreeVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getName());
        vo.setPath(menu.getPath());
        vo.setRedirect(menu.getRedirect());
        vo.setMenuType(menu.getMenuType());

        // 设置组件：目录类型(M)使用 BasicLayout 布局，菜单类型(C)使用实际组件路径
        String component = menu.getComponent();
        if (MenuTypeEnum.DIRECTORY.getCode().equals(menu.getMenuType())
                && (component == null || component.isBlank())) {
            vo.setComponent("BasicLayout");
        } else {
            vo.setComponent(component);
        }

        // 构建 meta
        SysMenuTreeVO.MenuMeta meta = new SysMenuTreeVO.MenuMeta();
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setAuthority(menu.getPermission());
        meta.setOrder(menu.getSort());
        meta.setAffixTab(menu.getIsAffix() != null && menu.getIsAffix() == 1);
        meta.setKeepAlive(menu.getIsCache() != null && menu.getIsCache() == 1);
        meta.setHideInMenu(menu.getVisible() != null && menu.getVisible() == 0);  // visible=0 表示隐藏
        vo.setMeta(meta);

        return vo;
    }

}
