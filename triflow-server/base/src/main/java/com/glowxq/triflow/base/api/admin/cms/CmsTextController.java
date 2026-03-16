package com.glowxq.triflow.base.api.admin.cms;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCreateDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextQueryDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextUpdateDTO;
import com.glowxq.triflow.base.cms.pojo.vo.CmsTextVO;
import com.glowxq.triflow.base.cms.service.CmsTextService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 文本内容管理控制器
 * <p>
 * 提供文本内容的增删改查接口。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Tag(name = "文本内容管理", description = "文本内容的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/cms/text")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.CMS)
public class CmsTextController extends BaseApi {

    private final CmsTextService cmsTextService;

    /**
     * 创建文本内容
     *
     * @param dto 创建请求参数
     * @return 操作结果
     */
    @Operation(summary = "创建文本内容", description = "创建一个新的文本内容")
    @SaCheckPermission("cms:text:create")
    @PostMapping("/create")
    public ApiResult<Void> create(@Valid @RequestBody CmsTextCreateDTO dto) {
        cmsTextService.create(dto);
        return ApiResult.success();
    }

    /**
     * 更新文本内容
     *
     * @param dto 更新请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新文本内容", description = "根据ID更新文本内容")
    @SaCheckPermission("cms:text:update")
    @PutMapping("/update")
    public ApiResult<Void> update(@Valid @RequestBody CmsTextUpdateDTO dto) {
        cmsTextService.update(dto);
        return ApiResult.success();
    }

    /**
     * 获取文本详情
     *
     * @param id 文本ID
     * @return 文本详情
     */
    @Operation(summary = "获取文本详情", description = "根据ID获取文本内容详情")
    @SaCheckPermission("cms:text:query")
    @GetMapping("/detail")
    public ApiResult<CmsTextVO> detail(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id) {
        CmsTextVO vo = cmsTextService.getDetail(id);
        return ApiResult.success(vo);
    }

    /**
     * 根据标识获取文本内容
     *
     * @param textKey 文本标识
     * @return 文本详情
     */
    @Operation(summary = "根据标识获取文本", description = "根据文本标识获取文本内容")
    @SaCheckPermission("cms:text:query")
    @GetMapping("/getByKey")
    public ApiResult<CmsTextVO> getByTextKey(
            @Parameter(description = "文本标识", required = true, example = "privacy-policy")
            @RequestParam String textKey) {
        CmsTextVO vo = cmsTextService.getByTextKey(textKey);
        return ApiResult.success(vo);
    }

    /**
     * 删除文本内容
     *
     * @param id 文本ID
     * @return 操作结果
     */
    @Operation(summary = "删除文本内容", description = "根据ID删除文本内容")
    @SaCheckPermission("cms:text:delete")
    @DeleteMapping("/delete")
    public ApiResult<Void> delete(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id) {
        cmsTextService.delete(id);
        return ApiResult.success();
    }

    /**
     * 批量删除文本内容
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除文本内容", description = "根据ID列表批量删除文本内容")
    @SaCheckPermission("cms:text:delete")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Void> deleteBatch(
            @Parameter(description = "文本ID列表", required = true)
            @RequestBody List<Long> ids) {
        cmsTextService.deleteBatch(ids);
        return ApiResult.success();
    }

    /**
     * 查询文本列表
     *
     * @param query 查询条件
     * @return 文本列表
     */
    @Operation(summary = "查询文本列表", description = "根据条件查询文本内容列表")
    @SaCheckPermission("cms:text:query")
    @PostMapping("/list")
    public ApiResult<List<CmsTextVO>> list(@RequestBody CmsTextQueryDTO query) {
        List<CmsTextVO> list = cmsTextService.getList(query);
        return ApiResult.success(list);
    }

    /**
     * 分页查询文本
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询文本", description = "分页获取文本内容列表")
    @SaCheckPermission("cms:text:query")
    @PostMapping("/page")
    public ApiResult<Page<CmsTextVO>> page(@RequestBody CmsTextQueryDTO query) {
        Page<CmsTextVO> page = cmsTextService.paginate(query);
        return ApiResult.success(page);
    }

    /**
     * 发布文本内容
     *
     * @param id 文本ID
     * @return 操作结果
     */
    @Operation(summary = "发布文本内容", description = "发布指定的文本内容")
    @SaCheckPermission("cms:text:publish")
    @PutMapping("/publish")
    public ApiResult<Void> publish(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id) {
        cmsTextService.publish(id);
        return ApiResult.success();
    }

    /**
     * 下架文本内容
     *
     * @param id 文本ID
     * @return 操作结果
     */
    @Operation(summary = "下架文本内容", description = "下架指定的文本内容")
    @SaCheckPermission("cms:text:unpublish")
    @PutMapping("/unpublish")
    public ApiResult<Void> unpublish(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id) {
        cmsTextService.unpublish(id);
        return ApiResult.success();
    }

    /**
     * 设置置顶状态
     *
     * @param id  文本ID
     * @param top 是否置顶
     * @return 操作结果
     */
    @Operation(summary = "设置置顶状态", description = "设置文本内容的置顶状态")
    @SaCheckPermission("cms:text:update")
    @PutMapping("/setTop")
    public ApiResult<Void> setTop(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id,
            @Parameter(description = "是否置顶", required = true, example = "1")
            @RequestParam Integer top) {
        cmsTextService.setTop(id, top);
        return ApiResult.success();
    }

    /**
     * 设置推荐状态
     *
     * @param id        文本ID
     * @param recommend 是否推荐
     * @return 操作结果
     */
    @Operation(summary = "设置推荐状态", description = "设置文本内容的推荐状态")
    @SaCheckPermission("cms:text:update")
    @PutMapping("/setRecommend")
    public ApiResult<Void> setRecommend(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id,
            @Parameter(description = "是否推荐", required = true, example = "1")
            @RequestParam Integer recommend) {
        cmsTextService.setRecommend(id, recommend);
        return ApiResult.success();
    }

    /**
     * 增加浏览次数
     *
     * @param id 文本ID
     * @return 操作结果
     */
    @Operation(summary = "增加浏览次数", description = "增加文本内容的浏览次数")
    @SaCheckPermission("cms:text:update")
    @PutMapping("/incrementView")
    public ApiResult<Void> incrementViewCount(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id) {
        cmsTextService.incrementViewCount(id);
        return ApiResult.success();
    }

    /**
     * 增加点赞次数
     *
     * @param id 文本ID
     * @return 操作结果
     */
    @Operation(summary = "增加点赞次数", description = "增加文本内容的点赞次数")
    @SaCheckPermission("cms:text:update")
    @PutMapping("/incrementLike")
    public ApiResult<Void> incrementLikeCount(
            @Parameter(description = "文本ID", required = true, example = "1")
            @RequestParam Long id) {
        cmsTextService.incrementLikeCount(id);
        return ApiResult.success();
    }

    /**
     * 检查文本标识是否存在
     *
     * @param textKey   文本标识
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    @Operation(summary = "检查文本标识", description = "检查文本标识是否已存在")
    @SaCheckPermission("cms:text:query")
    @GetMapping("/checkKey")
    public ApiResult<Boolean> checkTextKey(
            @Parameter(description = "文本标识", required = true, example = "privacy-policy")
            @RequestParam String textKey,
            @Parameter(description = "排除的ID")
            @RequestParam(required = false) Long excludeId) {
        boolean exists = cmsTextService.existsByTextKey(textKey, excludeId);
        return ApiResult.success(exists);
    }

}
