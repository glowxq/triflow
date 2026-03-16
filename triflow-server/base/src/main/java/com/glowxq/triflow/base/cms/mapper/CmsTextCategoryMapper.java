package com.glowxq.triflow.base.cms.mapper;

import com.glowxq.triflow.base.cms.entity.CmsTextCategory;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文本分类数据访问层
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Mapper
public interface CmsTextCategoryMapper extends BaseMapper<CmsTextCategory> {

    default CmsTextCategory selectByCategoryKey(String categoryKey) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsTextCategory.class)
                                      .eq(CmsTextCategory::getCategoryKey, categoryKey);
        return selectOneByQuery(qw);
    }

    default boolean existsByCategoryKey(String categoryKey, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsTextCategory.class)
                                      .eq(CmsTextCategory::getCategoryKey, categoryKey)
                                      .ne(CmsTextCategory::getId, excludeId, excludeId != null);
        return selectCountByQuery(qw) > 0;
    }

    default List<CmsTextCategory> selectByPid(Long pid) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsTextCategory.class)
                                      .eq(CmsTextCategory::getPid, pid)
                                      .eq(CmsTextCategory::getStatus, 1)
                                      .orderBy(CmsTextCategory::getSort, true);
        return selectListByQuery(qw);
    }

    default List<CmsTextCategory> selectAllEnabled() {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(CmsTextCategory.class)
                                      .eq(CmsTextCategory::getStatus, 1)
                                      .orderBy(CmsTextCategory::getSort, true);
        return selectListByQuery(qw);
    }

    default List<CmsTextCategory> selectByQuery(CmsTextCategoryQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    default Page<CmsTextCategory> paginateByQuery(int pageNum, int pageSize, CmsTextCategoryQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    default QueryWrapper buildQueryWrapper(CmsTextCategoryQueryDTO query) {
        return QueryWrapper.create()
                           .from(CmsTextCategory.class)
                           .like(CmsTextCategory::getCategoryName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                           .eq(CmsTextCategory::getPid, query.getPid(), query.getPid() != null)
                           .eq(CmsTextCategory::getStatus, query.getStatus(), query.getStatus() != null)
                           .orderBy(CmsTextCategory::getSort, true);
    }

}
