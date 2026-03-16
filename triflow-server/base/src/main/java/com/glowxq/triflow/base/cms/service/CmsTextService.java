package com.glowxq.triflow.base.cms.service;

import com.glowxq.triflow.base.cms.entity.CmsText;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCreateDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextQueryDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextUpdateDTO;
import com.glowxq.triflow.base.cms.pojo.vo.CmsTextVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 文本内容 Service 接口
 *
 * @author glowxq
 * @since 2025-01-24
 */
public interface CmsTextService extends IService<CmsText> {

    /**
     * 分页查询文本内容
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<CmsTextVO> paginate(CmsTextQueryDTO query);

    /**
     * 获取文本内容列表
     *
     * @param query 查询条件
     * @return 文本列表
     */
    List<CmsTextVO> getList(CmsTextQueryDTO query);

    /**
     * 获取文本详情
     *
     * @param id 文本ID
     * @return 文本详情
     */
    CmsTextVO getDetail(Long id);

    /**
     * 根据文本标识获取文本内容
     *
     * @param textKey 文本标识
     * @return 文本内容
     */
    CmsTextVO getByTextKey(String textKey);

    /**
     * 创建文本内容
     *
     * @param dto 创建参数
     * @return 文本ID
     */
    Long create(CmsTextCreateDTO dto);

    /**
     * 更新文本内容
     *
     * @param dto 更新参数
     * @return 是否成功
     */
    boolean update(CmsTextUpdateDTO dto);

    /**
     * 删除文本内容
     *
     * @param id 文本ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 批量删除文本内容
     *
     * @param ids 文本ID列表
     * @return 是否成功
     */
    boolean deleteBatch(List<Long> ids);

    /**
     * 发布文本内容
     *
     * @param id 文本ID
     * @return 是否成功
     */
    boolean publish(Long id);

    /**
     * 下架文本内容
     *
     * @param id 文本ID
     * @return 是否成功
     */
    boolean unpublish(Long id);

    /**
     * 置顶文本内容
     *
     * @param id  文本ID
     * @param top 是否置顶
     * @return 是否成功
     */
    boolean setTop(Long id, Integer top);

    /**
     * 推荐文本内容
     *
     * @param id        文本ID
     * @param recommend 是否推荐
     * @return 是否成功
     */
    boolean setRecommend(Long id, Integer recommend);

    /**
     * 增加浏览次数
     *
     * @param id 文本ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);

    /**
     * 增加点赞次数
     *
     * @param id 文本ID
     * @return 是否成功
     */
    boolean incrementLikeCount(Long id);

    /**
     * 检查文本标识是否存在
     *
     * @param textKey   文本标识
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean existsByTextKey(String textKey, Long excludeId);

}
