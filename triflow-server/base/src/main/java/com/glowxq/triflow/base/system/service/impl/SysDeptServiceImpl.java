package com.glowxq.triflow.base.system.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysDept;
import com.glowxq.triflow.base.system.mapper.SysDeptMapper;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptTreeVO;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptVO;
import com.glowxq.triflow.base.system.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 部门服务实现类
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper deptMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysDeptCreateDTO dto) {
        // DTO → Entity 转换
        SysDept dept = MapStructUtils.convert(dto, SysDept.class);

        // 设置默认值
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }
        if (dept.getSort() == null) {
            dept.setSort(0);
        }

        // 设置祖级路径
        if (dept.getParentId() == 0L) {
            dept.setAncestors("0");
        } else {
            SysDept parent = deptMapper.selectOneById(dept.getParentId());
            if (parent != null) {
                dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
            } else {
                dept.setAncestors("0");
            }
        }

        // 持久化
        deptMapper.insert(dept);

        log.info("创建部门成功, id={}, name={}", dept.getId(), dept.getDeptName());
        return dept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysDeptUpdateDTO dto) {
        SysDept existingDept = deptMapper.selectOneById(dto.getId());
        if (existingDept == null) {
            log.warn("更新部门失败, 部门不存在, id={}", dto.getId());
            return false;
        }

        // 不能将父部门设为自己
        if (dto.getParentId() != null && dto.getParentId().equals(dto.getId())) {
            throw new BusinessException("不能将父部门设为自己");
        }

        // DTO → Entity 转换
        SysDept dept = MapStructUtils.convert(dto, SysDept.class);

        // 更新祖级路径（如果父级变化）
        if (dto.getParentId() != null && !dto.getParentId().equals(existingDept.getParentId())) {
            if (dto.getParentId() == 0L) {
                dept.setAncestors("0");
            } else {
                SysDept parent = deptMapper.selectOneById(dto.getParentId());
                if (parent != null) {
                    dept.setAncestors(parent.getAncestors() + "," + dto.getParentId());
                }
            }
        }

        // 执行更新
        int rows = deptMapper.update(dept);
        log.info("更新部门, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    public SysDeptVO getById(Long id) {
        SysDept dept = deptMapper.selectOneById(id);
        if (dept == null) {
            return null;
        }
        return MapStructUtils.convert(dept, SysDeptVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        // 检查是否存在子部门
        if (deptMapper.existsChildren(id)) {
            throw new BusinessException("存在子部门，无法删除");
        }

        // 删除部门
        int rows = deptMapper.deleteById(id);
        log.info("删除部门, id={}, affected={}", id, rows);
        return rows > 0;
    }

    @Override
    public List<SysDeptVO> list(SysDeptQueryDTO query) {
        List<SysDept> depts = deptMapper.selectByQuery(query);
        return MapStructUtils.convert(depts, SysDeptVO.class);
    }

    @Override
    public List<SysDeptTreeVO> getDeptTree() {
        List<SysDept> depts = deptMapper.selectAllEnabled();
        return buildDeptTree(depts, 0L);
    }

    @Override
    public List<Long> getChildDeptIds(Long deptId) {
        // 使用递归 CTE 查询部门及其所有子孙部门的 ID
        List<Long> ids = deptMapper.selectChildDeptIds(deptId);
        // 如果部门不存在，递归查询会返回空列表，此时返回包含自身 ID 的列表
        return ids.isEmpty() ? List.of(deptId) : ids;
    }

    /**
     * 构建部门树
     */
    private List<SysDeptTreeVO> buildDeptTree(List<SysDept> depts, Long pid) {
        List<SysDeptTreeVO> result = new ArrayList<>();

        for (SysDept dept : depts) {
            if (Objects.equals(dept.getParentId(), pid)) {
                SysDeptTreeVO vo = MapStructUtils.convert(dept, SysDeptTreeVO.class);
                vo.setChildren(buildDeptTree(depts, dept.getId()));
                result.add(vo);
            }
        }

        return result;
    }

}
