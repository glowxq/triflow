package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.pojo.dto.SysDeptCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptTreeVO;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptVO;

import java.util.List;

/**
 * 部门服务接口
 * <p>
 * 定义部门业务逻辑的契约。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
public interface SysDeptService {

    /**
     * 创建部门
     *
     * @param dto 创建请求参数
     * @return 创建后的部门ID
     */
    Long create(SysDeptCreateDTO dto);

    /**
     * 更新部门
     *
     * @param dto 更新请求参数
     * @return 是否更新成功
     */
    boolean update(SysDeptUpdateDTO dto);

    /**
     * 根据ID获取部门详情
     *
     * @param id 部门ID
     * @return 部门 VO
     */
    SysDeptVO getById(Long id);

    /**
     * 根据ID删除部门
     *
     * @param id 部门ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 根据条件查询部门列表
     *
     * @param query 查询条件
     * @return 部门列表
     */
    List<SysDeptVO> list(SysDeptQueryDTO query);

    /**
     * 获取部门树形结构
     *
     * @return 部门树
     */
    List<SysDeptTreeVO> getDeptTree();

    /**
     * 获取所有子孙部门ID
     *
     * @param deptId 部门ID
     * @return 子孙部门ID列表（包含自身）
     */
    List<Long> getChildDeptIds(Long deptId);

}
