package com.glowxq.triflow.base.api.admin.cms;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryCreateDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryQueryDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryUpdateDTO;
import com.glowxq.triflow.base.cms.pojo.vo.CmsTextCategoryVO;
import com.glowxq.triflow.base.cms.service.CmsTextCategoryService;
import com.glowxq.common.core.util.MapStructUtils;
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
 * 文本分类管理控制器
 * <p>
 * 提供文本分类的增删改查接口。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Tag(name = "文本分类管理", description = "文本分类的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/cms/text-category")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.CMS)
public class CmsTextCategoryController extends BaseApi {

    private final CmsTextCategoryService cmsTextCategoryService;

    /**
     * 创建分类
     *
     * @param dto 创建请求参数
     * @return 操作结果
     */
    @Operation(summary = "创建分类", description = "创建一个新的文本分类")
    @SaCheckPermission("cms:textCategory:create")
    @PostMapping("/create")
    public ApiResult<Void> create(@Valid @RequestBody CmsTextCategoryCreateDTO dto) {
        cmsTextCategoryService.create(dto);
        return ApiResult.success();
    }

    /**
     * 更新分类
     *
     * @param dto 更新请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新分类", description = "根据ID更新文本分类")
    @SaCheckPermission("cms:textCategory:update")
    @PutMapping("/update")
    public ApiResult<Void> update(@Valid @RequestBody CmsTextCategoryUpdateDTO dto) {
        cmsTextCategoryService.update(dto);
        return ApiResult.success();
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @Operation(summary = "获取分类详情", description = "根据ID获取分类信息")
    @SaCheckPermission("cms:textCategory:query")
    @GetMapping("/detail")
    public ApiResult<CmsTextCategoryVO> detail(
            @Parameter(description = "分类ID", required = true, example = "1")
            @RequestParam Long id) {
        CmsTextCategoryVO vo = cmsTextCategoryService.getDetail(id);
        return ApiResult.success(vo);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "删除分类", description = "根据ID删除分类")
    @SaCheckPermission("cms:textCategory:delete")
    @DeleteMapping("/delete")
    public ApiResult<Void> delete(
            @Parameter(description = "分类ID", required = true, example = "1")
            @RequestParam Long id) {
        cmsTextCategoryService.delete(id);
        return ApiResult.success();
    }

    /**
     * 批量删除分类
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除分类", description = "根据ID列表批量删除分类")
    @SaCheckPermission("cms:textCategory:delete")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Void> deleteBatch(
            @Parameter(description = "分类ID列表", required = true)
            @RequestBody List<Long> ids) {
        cmsTextCategoryService.deleteBatch(ids);
        return ApiResult.success();
    }

    /**
     * 查询分类列表
     *
     * @param query 查询条件
     * @return 分类列表
     */
    @Operation(summary = "查询分类列表", description = "根据条件查询分类列表")
    @SaCheckPermission("cms:textCategory:query")
    @PostMapping("/list")
    public ApiResult<List<CmsTextCategoryVO>> list(@RequestBody CmsTextCategoryQueryDTO query) {
        List<CmsTextCategoryVO> list = cmsTextCategoryService.getList(query);
        return ApiResult.success(list);
    }

    /**
     * 获取分类树形结构
     *
     * @param query 查询条件
     * @return 分类树
     */
    @Operation(summary = "获取分类树", description = "获取分类的树形结构")
    @SaCheckPermission("cms:textCategory:query")
    @PostMapping("/tree")
    public ApiResult<List<CmsTextCategoryVO>> tree(@RequestBody CmsTextCategoryQueryDTO query) {
        List<CmsTextCategoryVO> tree = cmsTextCategoryService.getTree(query);
        return ApiResult.success(tree);
    }

    /**
     * 根据分类标识获取分类
     *
     * @param categoryKey 分类标识
     * @return 分类详情
     */
    @Operation(summary = "根据标识获取分类", description = "根据分类标识获取分类信息")
    @SaCheckPermission("cms:textCategory:query")
    @GetMapping("/getByKey")
    public ApiResult<CmsTextCategoryVO> getByKey(
            @Parameter(description = "分类标识", required = true, example = "notice")
            @RequestParam String categoryKey) {
        var entity = cmsTextCategoryService.getByCategoryKey(categoryKey);
        if (entity == null) {
            return ApiResult.success(null);
        }
        return ApiResult.success(MapStructUtils.convert(entity, CmsTextCategoryVO.class));
    }

    /**
     * 获取所有启用的分类
     *
     * @return 分类列表
     */
    @Operation(summary = "获取所有启用的分类", description = "获取所有启用的分类列表")
    @SaCheckPermission("cms:textCategory:query")
    @GetMapping("/listAll")
    public ApiResult<List<CmsTextCategoryVO>> listAll() {
        List<CmsTextCategoryVO> list = cmsTextCategoryService.listAllEnabled();
        return ApiResult.success(list);
    }

    /**
     * 检查分类标识是否存在
     *
     * @param categoryKey 分类标识
     * @param excludeId   排除的ID
     * @return 是否存在
     */
    @Operation(summary = "检查分类标识", description = "检查分类标识是否已存在")
    @SaCheckPermission("cms:textCategory:query")
    @GetMapping("/checkKey")
    public ApiResult<Boolean> checkCategoryKey(
            @Parameter(description = "分类标识", required = true, example = "notice")
            @RequestParam String categoryKey,
            @Parameter(description = "排除的ID")
            @RequestParam(required = false) Long excludeId) {
        boolean exists = cmsTextCategoryService.existsByCategoryKey(categoryKey, excludeId);
        return ApiResult.success(exists);
    }

}
