package com.glowxq.triflow.base.api.admin.file;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.oss.config.S3Configuration;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigCreateDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigQueryDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigUpdateDTO;
import com.glowxq.triflow.base.file.pojo.vo.FileConfigVO;
import com.glowxq.triflow.base.file.service.FileConfigService;
import com.mybatisflex.core.paginate.Page;
import com.glowxq.common.core.util.MapStructUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 文件存储配置控制器
 * <p>
 * 提供文件存储配置的增删改查接口。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Tag(name = "文件存储配置", description = "文件存储配置的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/file/config")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.File)
public class FileConfigController extends BaseApi {

    private final FileConfigService configService;
    private final S3Configuration s3Configuration;

    /**
     * 创建配置
     *
     * @param dto 创建请求参数
     * @return 操作结果
     */
    @Operation(summary = "创建配置", description = "创建一个新的文件存储配置")
    @SaCheckPermission("file:config:create")
    @PostMapping("/create")
    public ApiResult<Void> create(@Valid @RequestBody FileConfigCreateDTO dto) {
        configService.create(dto);
        return ApiResult.success();
    }

    /**
     * 更新配置
     *
     * @param dto 更新请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新配置", description = "根据ID更新文件存储配置")
    @SaCheckPermission("file:config:update")
    @PutMapping("/update")
    public ApiResult<Void> update(@Valid @RequestBody FileConfigUpdateDTO dto) {
        configService.update(dto);
        return ApiResult.success();
    }

    /**
     * 获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    @Operation(summary = "获取配置详情", description = "根据ID获取配置信息")
    @SaCheckPermission("file:config:query")
    @GetMapping("/detail")
    public ApiResult<FileConfigVO> detail(
            @Parameter(description = "配置ID", required = true, example = "1")
            @RequestParam Long id) {
        FileConfigVO detail = configService.getById(id);
        return ApiResult.success(detail);
    }

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 操作结果
     */
    @Operation(summary = "删除配置", description = "根据ID删除配置")
    @SaCheckPermission("file:config:delete")
    @DeleteMapping("/delete")
    public ApiResult<Void> delete(
            @Parameter(description = "配置ID", required = true, example = "1")
            @RequestParam Long id) {
        configService.deleteById(id);
        return ApiResult.success();
    }

    /**
     * 批量删除配置
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除配置", description = "根据ID列表批量删除配置")
    @SaCheckPermission("file:config:delete")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Void> deleteBatch(
            @Parameter(description = "配置ID列表", required = true)
            @RequestBody List<Long> ids) {
        configService.deleteByIds(ids);
        return ApiResult.success();
    }

    /**
     * 查询配置列表
     *
     * @param query 查询条件
     * @return 配置列表
     */
    @Operation(summary = "查询配置列表", description = "根据条件查询配置列表")
    @SaCheckPermission("file:config:query")
    @PostMapping("/list")
    public ApiResult<List<FileConfigVO>> list(@RequestBody FileConfigQueryDTO query) {
        List<FileConfigVO> list = configService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 分页查询配置
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询配置", description = "分页获取配置列表")
    @SaCheckPermission("file:config:query")
    @PostMapping("/page")
    public ApiResult<Page<FileConfigVO>> page(@RequestBody FileConfigQueryDTO query) {
        Page<FileConfigVO> page = configService.page(query);
        return ApiResult.success(page);
    }

    /**
     * 获取所有启用的配置
     *
     * @return 配置列表
     */
    @Operation(summary = "获取所有启用的配置", description = "获取所有启用的配置列表")
    @SaCheckPermission("file:config:query")
    @GetMapping("/listAll")
    public ApiResult<List<FileConfigVO>> listAll() {
        List<FileConfigVO> list = configService.listAllEnabled();
        return ApiResult.success(list);
    }

    /**
     * 设置默认配置
     *
     * @param id 配置ID
     * @return 操作结果
     */
    @Operation(summary = "设置默认配置", description = "将指定配置设为默认配置")
    @SaCheckPermission("file:config:setDefault")
    @PutMapping("/setDefault")
    public ApiResult<Void> setDefault(
            @Parameter(description = "配置ID", required = true, example = "1")
            @RequestParam Long id) {
        configService.setDefault(id);
        return ApiResult.success();
    }

    /**
     * 获取默认配置
     *
     * @return 默认配置
     */
    @Operation(summary = "获取默认配置", description = "获取当前默认的文件存储配置")
    @SaCheckPermission("file:config:query")
    @GetMapping("/getDefault")
    public ApiResult<FileConfigVO> getDefault() {
        var config = configService.getDefaultConfig();
        if (config == null) {
            return ApiResult.success(null);
        }
        return ApiResult.success(MapStructUtils.convert(config, FileConfigVO.class));
    }

    /**
     * 测试配置连接
     *
     * @param id 配置ID
     * @return 测试结果
     */
    @Operation(summary = "测试连接", description = "测试文件存储配置的连接是否正常")
    @SaCheckPermission("file:config:test")
    @PostMapping("/testConnection")
    public ApiResult<Boolean> testConnection(
            @Parameter(description = "配置ID", required = true, example = "1")
            @RequestParam Long id) {
        // TODO: 实现具体的连接测试逻辑
        return ApiResult.success(true);
    }

    /**
     * 获取 OSS 配置模板
     * <p>
     * 从 yaml 配置文件读取默认的 OSS 配置，凭证信息会脱敏显示。
     * </p>
     *
     * @return OSS 配置模板
     */
    @Operation(summary = "获取OSS配置模板", description = "获取从配置文件读取的默认OSS配置（凭证脱敏）")
    @SaCheckPermission("file:config:query")
    @GetMapping("/getOssTemplate")
    public ApiResult<Map<String, Object>> getOssTemplate() {
        Map<String, Object> template = new HashMap<>();
        template.put("provider", s3Configuration.getProvider().name());
        template.put("endpoint", s3Configuration.getEndpoint());
        template.put("accessKey", maskCredential(s3Configuration.getAccessKey()));
        template.put("secretKey", maskCredential(s3Configuration.getSecretKey()));
        template.put("bucketName", s3Configuration.getBucketName());
        template.put("domain", s3Configuration.getDomain());
        template.put("scheme", s3Configuration.isUseHttps() ? "https" : "http");
        template.put("storageType", "oss");
        return ApiResult.success(template);
    }

    /**
     * 凭证脱敏
     * 只显示前4位和后4位，中间用 **** 替代
     */
    private String maskCredential(String credential) {
        if (credential == null || credential.length() <= 8) {
            return "****";
        }
        return credential.substring(0, 4) + "****" + credential.substring(credential.length() - 4);
    }

}
