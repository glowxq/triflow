package com.glowxq.triflow.base.template.service.impl;

import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.triflow.base.template.entity.Article;
import com.glowxq.triflow.base.template.mapper.ArticleMapper;
import com.glowxq.triflow.base.template.pojo.dto.ArticleCreateDTO;
import com.glowxq.triflow.base.template.pojo.dto.ArticleQueryDTO;
import com.glowxq.triflow.base.template.pojo.dto.ArticleUpdateDTO;
import com.glowxq.triflow.base.template.pojo.vo.ArticleDetailVO;
import com.glowxq.triflow.base.template.pojo.vo.ArticleExcelVO;
import com.glowxq.triflow.base.template.pojo.vo.ArticleVO;
import com.glowxq.triflow.base.template.service.ArticleService;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章服务实现类
 * <p>
 * 实现文章的业务逻辑，仅负责业务编排和对象转换。
 * 所有 SQL 逻辑（含 QueryWrapper）封装在 Mapper 层。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ArticleCreateDTO dto) {
        // DTO → Entity 转换
        Article article = MapStructUtils.convert(dto, Article.class);

        // 设置默认值
        if (article.getStatus() == null) {
            article.setStatus(0);  // 默认草稿状态
        }
        article.setViewCount(0);  // 初始浏览次数

        // 持久化
        articleMapper.insert(article);

        log.info("创建文章成功, id={}, title={}", article.getId(), article.getTitle());
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(ArticleUpdateDTO dto) {
        // 检查文章是否存在
        Article existingArticle = articleMapper.selectOneById(dto.getId());
        if (existingArticle == null) {
            log.warn("更新文章失败, 文章不存在, id={}", dto.getId());
            return false;
        }

        // DTO → Entity 转换（仅更新非空字段）
        Article article = MapStructUtils.convert(dto, Article.class);

        // 执行更新
        int rows = articleMapper.update(article);

        log.info("更新文章, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    public ArticleDetailVO getById(Long id) {
        Article article = articleMapper.selectOneById(id);
        if (article == null) {
            log.debug("查询文章详情, 文章不存在, id={}", id);
            return null;
        }

        // Entity → VO 转换
        return MapStructUtils.convert(article, ArticleDetailVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        int rows = articleMapper.deleteById(id);
        log.info("删除文章, id={}, affected={}", id, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        int rows = articleMapper.deleteBatchByIds(ids);
        log.info("批量删除文章, ids={}, affected={}", ids, rows);
        return rows > 0;
    }

    @Override
    public List<ArticleVO> list(ArticleQueryDTO query) {
        // 查询逻辑封装在 Mapper 层
        List<Article> articles = articleMapper.selectByQuery(query);

        // Entity List → VO List 批量转换
        return MapStructUtils.convert(articles, ArticleVO.class);
    }

    @Override
    public Page<ArticleVO> page(ArticleQueryDTO query) {
        // 分页查询逻辑封装在 Mapper 层
        Page<Article> articlePage = articleMapper.selectPageByQuery(
            query.getPageNum(),
            query.getPageSize(),
            query
        );

        // 转换分页结果中的实体
        Page<ArticleVO> voPage = new Page<>();
        voPage.setPageNumber(articlePage.getPageNumber());
        voPage.setPageSize(articlePage.getPageSize());
        voPage.setTotalPage(articlePage.getTotalPage());
        voPage.setTotalRow(articlePage.getTotalRow());
        voPage.setRecords(MapStructUtils.convert(articlePage.getRecords(), ArticleVO.class));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        Article article = articleMapper.selectOneById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.update(article);
            log.debug("增加文章浏览次数, id={}, viewCount={}", id, article.getViewCount());
        }
    }

    // ==================== 导入导出 ====================

    @Override
    public void export(ArticleQueryDTO query, HttpServletResponse response) {
        // 查询数据
        List<Article> articles = articleMapper.selectByQuery(query);

        // Entity → Excel VO 转换
        List<ArticleExcelVO> excelList = MapStructUtils.convert(articles, ArticleExcelVO.class);

        // 设置响应头
        setExcelResponseHeader(response, "文章数据");

        try {
            // 导出 Excel
            ExcelUtils.exportExcel(excelList, "文章列表", ArticleExcelVO.class, response.getOutputStream());
            log.info("导出文章数据完成, 数量: {}", excelList.size());
        } catch (IOException e) {
            log.error("导出文章数据失败", e);
            throw new RuntimeException("导出文章数据失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelResult<ArticleExcelVO> importData(MultipartFile file) throws IOException {
        // 导入并验证
        ExcelResult<ArticleExcelVO> result = ExcelUtils.importExcel(
            file.getInputStream(),
            ArticleExcelVO.class,
            true  // 验证表头
        );

        // 处理导入数据
        List<ArticleExcelVO> dataList = result.getList();
        for (ArticleExcelVO excelVO : dataList) {
            // Excel VO → Entity 转换
            Article article = MapStructUtils.convert(excelVO, Article.class);

            // 设置默认值（导入的文章默认为草稿状态）
            if (article.getStatus() == null) {
                article.setStatus(0);
            }
            if (article.getViewCount() == null) {
                article.setViewCount(0);
            }

            // 保存数据
            articleMapper.insert(article);
        }

        log.info("导入文章数据完成, 成功: {}, 失败: {}",
            result.getList().size(), result.getErrorList().size());

        return result;
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) {
        // 设置响应头
        setExcelResponseHeader(response, "文章导入模板");

        try {
            // 导出空数据作为模板
            ExcelUtils.exportExcel(new ArrayList<>(), "模板", ArticleExcelVO.class, response.getOutputStream());
            log.info("下载文章导入模板完成");
        } catch (IOException e) {
            log.error("下载导入模板失败", e);
            throw new RuntimeException("下载导入模板失败", e);
        }
    }

    /**
     * 设置 Excel 响应头
     *
     * @param response HTTP 响应
     * @param fileName 文件名（不含后缀）
     */
    private void setExcelResponseHeader(HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");
    }

}
