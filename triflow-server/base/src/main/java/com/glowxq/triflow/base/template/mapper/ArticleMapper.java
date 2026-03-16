package com.glowxq.triflow.base.template.mapper;

import com.glowxq.triflow.base.template.entity.Article;
import com.glowxq.triflow.base.template.pojo.dto.ArticleQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文章数据访问层
 * <p>
 * 提供文章的数据库操作方法，所有 SQL 逻辑封装在此层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 根据查询条件获取文章列表
     *
     * @param query 查询条件
     *
     * @return 文章列表
     */
    default List<Article> selectByQuery(ArticleQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 分页查询文章列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param query    查询条件
     *
     * @return 分页结果
     */
    default Page<Article> selectPageByQuery(int pageNum, int pageSize, ArticleQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 根据标题模糊查询文章
     *
     * @param title 标题关键词
     *
     * @return 文章列表
     */
    default List<Article> selectByTitle(String title) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(Article.class)
                                      .like(Article::getTitle, title, StringUtils.isNotBlank(title))
                                      .orderBy(Article::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据分类ID查询文章列表
     *
     * @param categoryId 分类ID
     *
     * @return 文章列表
     */
    default List<Article> selectByCategoryId(Long categoryId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(Article.class)
                                      .eq(Article::getCategoryId, categoryId, categoryId != null)
                                      .eq(Article::getStatus, 1)  // 只查询已发布的
                                      .orderBy(Article::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 构建查询条件
     *
     * @param query 查询 DTO
     *
     * @return QueryWrapper
     */
    private QueryWrapper buildQueryWrapper(ArticleQueryDTO query) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(Article.class)
                                      .like(Article::getTitle, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                                      .eq(Article::getCategoryId, query.getCategoryId(), query.getCategoryId() != null)
                                      .eq(Article::getStatus, query.getStatus(), query.getStatus() != null)
                                      .ge(Article::getCreateTime, query.getStartTime(), query.getStartTime() != null)
                                      .le(Article::getCreateTime, query.getEndTime(), query.getEndTime() != null)
                                      .orderBy(Article::getCreateTime, false);  // false = DESC
        return qw;
    }

}
