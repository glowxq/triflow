package com.glowxq.triflow.base.cms.service;

import com.glowxq.triflow.base.cms.entity.CmsTextCategory;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryCreateDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryQueryDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryUpdateDTO;
import com.glowxq.triflow.base.cms.pojo.vo.CmsTextCategoryVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 文本分类 Service 接口
 *
 * @author glowxq
 * @since 2025-01-24
 */
public interface CmsTextCategoryService extends IService<CmsTextCategory> {

    /**
     * 获取分类树形结构
     *
     * @param query 查询条件
     * @return 分类树
     */
    List<CmsTextCategoryVO> getTree(CmsTextCategoryQueryDTO query);

    /**
     * 获取分类列表
     *
     * @param query 查询条件
     * @return 分类列表
     */
    List<CmsTextCategoryVO> getList(CmsTextCategoryQueryDTO query);

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    CmsTextCategoryVO getDetail(Long id);

    /**
     * 创建分类
     *
     * @param dto 创建参数
     * @return 分类ID
     */
    Long create(CmsTextCategoryCreateDTO dto);

    /**
     * 更新分类
     *
     * @param dto 更新参数
     * @return 是否成功
     */
    boolean update(CmsTextCategoryUpdateDTO dto);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 批量删除分类
     *
     * @param ids 分类ID列表
     * @return 是否成功
     */
    boolean deleteBatch(List<Long> ids);

    /**
     * 检查分类标识是否存在
     *
     * @param categoryKey 分类标识
     * @param excludeId   排除的ID
     * @return 是否存在
     */
    boolean existsByCategoryKey(String categoryKey, Long excludeId);

    /**
     * 检查分类下是否有子分类
     *
     * @param id 分类ID
     * @return 是否有子分类
     */
    boolean hasChildren(Long id);

    /**
     * 检查分类下是否有文本内容
     *
     * @param id 分类ID
     * @return 是否有文本内容
     */
    boolean hasTexts(Long id);

    /**
     * 根据分类标识获取分类
     *
     * @param categoryKey 分类标识
     * @return 分类实体
     */
    CmsTextCategory getByCategoryKey(String categoryKey);

    /**
     * 获取所有启用的分类
     *
     * @return 分类列表
     */
    List<CmsTextCategoryVO> listAllEnabled();

}
