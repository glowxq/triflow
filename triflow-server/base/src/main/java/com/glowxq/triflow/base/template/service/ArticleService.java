package com.glowxq.triflow.base.template.service;

import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.triflow.base.template.pojo.dto.ArticleCreateDTO;
import com.glowxq.triflow.base.template.pojo.dto.ArticleQueryDTO;
import com.glowxq.triflow.base.template.pojo.dto.ArticleUpdateDTO;
import com.glowxq.triflow.base.template.pojo.vo.ArticleDetailVO;
import com.glowxq.triflow.base.template.pojo.vo.ArticleExcelVO;
import com.glowxq.triflow.base.template.pojo.vo.ArticleVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文章服务接口
 * <p>
 * 定义文章业务逻辑的契约，Service 层只负责业务逻辑编排，
 * 不包含任何 SQL 或 QueryWrapper 逻辑（这些封装在 Mapper 层）。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
public interface ArticleService {

    /**
     * 创建文章
     *
     * @param dto 创建请求参数
     *
     * @return 创建后的文章ID
     */
    Long create(ArticleCreateDTO dto);

    /**
     * 更新文章
     *
     * @param dto 更新请求参数
     *
     * @return 是否更新成功
     */
    boolean update(ArticleUpdateDTO dto);

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     *
     * @return 文章详情 VO
     */
    ArticleDetailVO getById(Long id);

    /**
     * 根据ID删除文章
     *
     * @param id 文章ID
     *
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除文章
     *
     * @param ids 文章ID列表
     *
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids);

    /**
     * 根据条件查询文章列表
     *
     * @param query 查询条件
     *
     * @return 文章列表（不含正文）
     */
    List<ArticleVO> list(ArticleQueryDTO query);

    /**
     * 分页查询文章列表
     *
     * @param query 查询条件（包含分页参数）
     *
     * @return 分页结果
     */
    Page<ArticleVO> page(ArticleQueryDTO query);

    /**
     * 增加文章浏览次数
     *
     * @param id 文章ID
     */
    void incrementViewCount(Long id);

    // ==================== 导入导出 ====================

    /**
     * 导出文章数据
     *
     * @param query    查询条件
     * @param response HTTP 响应
     */
    void export(ArticleQueryDTO query, HttpServletResponse response);

    /**
     * 导入文章数据
     *
     * @param file Excel 文件
     *
     * @return 导入结果
     *
     * @throws IOException 文件读取异常
     */
    ExcelResult<ArticleExcelVO> importData(MultipartFile file) throws IOException;

    /**
     * 下载导入模板
     *
     * @param response HTTP 响应
     */
    void downloadImportTemplate(HttpServletResponse response);

}
