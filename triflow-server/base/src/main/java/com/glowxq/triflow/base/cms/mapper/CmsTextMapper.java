package com.glowxq.triflow.base.cms.mapper;

import com.glowxq.triflow.base.cms.entity.CmsText;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文本内容数据访问层
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Mapper
public interface CmsTextMapper extends BaseMapper<CmsText> {

    default CmsText selectByTextKey(String textKey) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsText.class)
                                      .eq(CmsText::getTextKey, textKey)
                                      .eq(CmsText::getStatus, 1)
                                      .orderBy(CmsText::getVersion, false);
        return selectOneByQuery(qw);
    }

    default boolean existsByTextKey(String textKey, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsText.class)
                                      .eq(CmsText::getTextKey, textKey)
                                      .ne(CmsText::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    default List<CmsText> selectByCategoryId(Long categoryId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsText.class)
                                      .eq(CmsText::getCategoryId, categoryId)
                                      .eq(CmsText::getStatus, 1)
                                      .orderBy(CmsText::getSort, true);
        return selectListByQuery(qw);
    }

    default List<CmsText> selectByTextType(String textType) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsText.class)
                                      .eq(CmsText::getTextType, textType)
                                      .eq(CmsText::getStatus, 1)
                                      .orderBy(CmsText::getSort, true);
        return selectListByQuery(qw);
    }

    default List<CmsText> selectPublished() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsText.class)
                                      .eq(CmsText::getStatus, 1)
                                      .orderBy(CmsText::getTop, false)
                                      .orderBy(CmsText::getSort, true)
                                      .orderBy(CmsText::getPublishTime, false);
        return selectListByQuery(qw);
    }

    default List<CmsText> selectByQuery(CmsTextQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    default Page<CmsText> paginateByQuery(int pageNum, int pageSize, CmsTextQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    default QueryWrapper buildQueryWrapper(CmsTextQueryDTO query) {
        QueryWrapper qw = QueryWrapper.create()
                           .from(CmsText.class)
                           .like(CmsText::getTextTitle, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(CmsText::getCategoryId, query.getCategoryId(), query.getCategoryId() != null)
                           .eq(CmsText::getTextType, query.getTextType(), StringUtils.isNotBlank(query.getTextType()))
                           .eq(CmsText::getStatus, query.getStatus(), query.getStatus() != null)
                           .eq(CmsText::getTop, query.getTop(), query.getTop() != null)
                           .eq(CmsText::getRecommend, query.getRecommend(), query.getRecommend() != null)
                           .ge(CmsText::getCreateTime, query.getStartTime(), query.getStartTime() != null)
                           .le(CmsText::getCreateTime, query.getEndTime(), query.getEndTime() != null)
                           .orderBy(CmsText::getTop, false)
                           .orderBy(CmsText::getSort, true)
                           .orderBy(CmsText::getCreateTime, false);
        return qw;
    }

    default int incrementViewCount(Long id) {
        CmsText text = selectOneById(id);
        if (text == null) {
            return 0;
        }
        CmsText update = new CmsText();
        update.setId(id);
        update.setViewCount(text.getViewCount() == null ? 1 : text.getViewCount() + 1);
        return update(update);
    }

}
