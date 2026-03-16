package com.glowxq.triflow.base.api.admin.file;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.common.oss.model.PresignedUploadResult;
import com.glowxq.triflow.base.file.pojo.dto.ConfirmUploadDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileInfoQueryDTO;
import com.glowxq.triflow.base.file.pojo.dto.PresignRequestDTO;
import com.glowxq.triflow.base.file.pojo.vo.FileInfoExcelVO;
import com.glowxq.triflow.base.file.pojo.vo.FileInfoVO;
import com.glowxq.triflow.base.file.service.FileInfoService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 文件信息控制器
 * <p>
 * 提供文件的预签名上传和信息管理接口。
 * 文件上传采用前端直传 OSS 模式，不经过服务端。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Tag(name = "文件管理", description = "文件预签名上传和信息管理接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/file/info")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.File)
public class FileInfoController extends BaseApi {

    private final FileInfoService fileInfoService;

    // ==================== 预签名上传接口 ====================

    /**
     * 获取预签名上传URL
     * <p>
     * 前端使用返回的 uploadUrl 直接上传文件到 OSS。
     * </p>
     *
     * @param dto 预签名请求参数
     * @return 预签名上传结果
     */
    @Operation(summary = "获取预签名上传URL", description = "获取OSS预签名URL，用于前端直传")
    @SaCheckPermission("file:info:upload")
    @PostMapping("/presign")
    public ApiResult<PresignedUploadResult> presign(@Valid @RequestBody PresignRequestDTO dto) {
        PresignedUploadResult result = fileInfoService.generatePresignedUrl(dto);
        return ApiResult.success(result);
    }

    /**
     * 确认上传完成
     * <p>
     * 前端直传 OSS 成功后，调用此接口保存文件记录。
     * </p>
     *
     * @param dto 确认上传参数
     * @return 文件信息
     */
    @Operation(summary = "确认上传完成", description = "前端直传成功后，保存文件记录到数据库")
    @SaCheckPermission("file:info:upload")
    @PostMapping("/confirmUpload")
    public ApiResult<FileInfoVO> confirmUpload(@Valid @RequestBody ConfirmUploadDTO dto) {
        FileInfoVO vo = fileInfoService.confirmUpload(dto);
        return ApiResult.success(vo);
    }

    // ==================== 查询接口 ====================

    /**
     * 获取文件详情
     *
     * @param id 文件ID
     * @return 文件详情
     */
    @Operation(summary = "获取文件详情", description = "根据ID获取文件信息")
    @SaCheckPermission("file:info:query")
    @GetMapping("/detail")
    public ApiResult<FileInfoVO> detail(
            @Parameter(description = "文件ID", required = true, example = "1")
            @RequestParam Long id) {
        FileInfoVO detail = fileInfoService.getById(id);
        return ApiResult.success(detail);
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     * @return 操作结果
     */
    @Operation(summary = "删除文件", description = "根据ID删除文件")
    @SaCheckPermission("file:info:delete")
    @DeleteMapping("/delete")
    public ApiResult<Void> delete(
            @Parameter(description = "文件ID", required = true, example = "1")
            @RequestParam Long id) {
        fileInfoService.deleteById(id);
        return ApiResult.success();
    }

    /**
     * 批量删除文件
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除文件", description = "根据ID列表批量删除文件")
    @SaCheckPermission("file:info:delete")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Void> deleteBatch(
            @Parameter(description = "文件ID列表", required = true)
            @RequestBody List<Long> ids) {
        fileInfoService.deleteByIds(ids);
        return ApiResult.success();
    }


    /**
     * 物理删除文件（同时删除OSS文件）
     *
     * @param id 文件ID
     * @return 操作结果
     */
    @Operation(summary = "物理删除文件", description = "物理删除文件，同时删除OSS存储的实际文件")
    @SaCheckPermission("file:info:delete")
    @DeleteMapping("/physicalDelete")
    public ApiResult<Void> physicalDelete(
            @Parameter(description = "文件ID", required = true, example = "1")
            @RequestParam Long id) {
        fileInfoService.physicalDeleteById(id);
        return ApiResult.success();
    }

    /**
     * 批量物理删除文件（同时删除OSS文件）
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量物理删除文件", description = "批量物理删除文件，同时删除OSS存储的实际文件")
    @SaCheckPermission("file:info:delete")
    @DeleteMapping("/physicalDeleteBatch")
    public ApiResult<Void> physicalDeleteBatch(
            @Parameter(description = "文件ID列表", required = true)
            @RequestBody List<Long> ids) {
        fileInfoService.physicalDeleteByIds(ids);
        return ApiResult.success();
    }

    /**
     * 查询文件列表
     *
     * @param query 查询条件
     * @return 文件列表
     */
    @Operation(summary = "查询文件列表", description = "根据条件查询文件列表")
    @SaCheckPermission("file:info:query")
    @PostMapping("/list")
    public ApiResult<List<FileInfoVO>> list(@RequestBody FileInfoQueryDTO query) {
        List<FileInfoVO> list = fileInfoService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 分页查询文件
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询文件", description = "分页获取文件列表")
    @SaCheckPermission("file:info:query")
    @PostMapping("/page")
    public ApiResult<Page<FileInfoVO>> page(@RequestBody FileInfoQueryDTO query) {
        Page<FileInfoVO> page = fileInfoService.page(query);
        return ApiResult.success(page);
    }

    /**
     * 根据业务查询文件
     *
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @return 文件列表
     */
    @Operation(summary = "根据业务查询文件", description = "根据业务类型和业务ID查询关联的文件")
    @SaCheckPermission("file:info:query")
    @GetMapping("/listByBiz")
    public ApiResult<List<FileInfoVO>> listByBiz(
            @Parameter(description = "业务类型", required = true, example = "article")
            @RequestParam String bizType,
            @Parameter(description = "业务ID", required = true, example = "100")
            @RequestParam Long bizId) {
        List<FileInfoVO> list = fileInfoService.listByBiz(bizType, bizId);
        return ApiResult.success(list);
    }

    /**
     * 根据分类查询文件
     *
     * @param category 文件分类
     * @return 文件列表
     */
    @Operation(summary = "根据分类查询文件", description = "获取指定分类下的所有文件")
    @SaCheckPermission("file:info:query")
    @GetMapping("/listByCategory")
    public ApiResult<List<FileInfoVO>> listByCategory(
            @Parameter(description = "文件分类", required = true, example = "image")
            @RequestParam String category) {
        List<FileInfoVO> list = fileInfoService.listByCategory(category);
        return ApiResult.success(list);
    }

    /**
     * 获取文件预览/下载URL
     * <p>
     * 对于私有存储的文件，返回带签名的临时访问URL。
     * </p>
     *
     * @param id 文件ID
     * @return 预览/下载URL
     */
    @Operation(summary = "获取预览URL", description = "获取文件的预览/下载URL（私有文件返回预签名URL）")
    @SaCheckPermission("file:info:download")
    @GetMapping("/previewUrl")
    public ApiResult<String> previewUrl(
            @Parameter(description = "文件ID", required = true, example = "1")
            @RequestParam Long id) {
        String url = fileInfoService.getPreviewUrl(id);
        return ApiResult.success(url);
    }


    // ==================== 导出 ====================

    /**
     * 导出文件列表
     *
     * @param query    查询条件
     * @param response HTTP响应
     */
    @Operation(summary = "导出文件列表", description = "导出文件数据到Excel文件")
    @SaCheckPermission("file:info:query")
    @PostMapping("/export")
    public void export(@RequestBody FileInfoQueryDTO query, HttpServletResponse response) throws IOException {
        List<FileInfoVO> list = fileInfoService.list(query);
        List<FileInfoExcelVO> excelList = MapStructUtils.convert(list, FileInfoExcelVO.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("文件列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(excelList, "文件列表", FileInfoExcelVO.class, response.getOutputStream());
    }

}
