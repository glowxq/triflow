package com.glowxq.triflow.base.api.anon;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.oss.model.PresignedUploadResult;
import com.glowxq.triflow.base.cms.pojo.vo.CmsTextVO;
import com.glowxq.triflow.base.cms.service.CmsTextService;
import com.glowxq.triflow.base.file.enums.PublicUploadBizTypeEnum;
import com.glowxq.triflow.base.file.pojo.dto.PresignRequestDTO;
import com.glowxq.triflow.base.file.service.FileInfoService;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptTreeVO;
import com.glowxq.triflow.base.system.service.SysConfigService;
import com.glowxq.triflow.base.system.service.SysDeptService;
import com.glowxq.triflow.base.system.service.SysSwitchService;
import com.glowxq.triflow.base.wechat.pojo.vo.WechatTabbarVO;
import com.glowxq.triflow.base.wechat.service.WechatTabbarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AnonApi;

/**
 * 公共配置控制器
 * <p>
 * 提供无需认证的公共配置接口，如开关状态查询等。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Tag(name = "公共配置", description = "无需认证的公共配置接口")
@AnonApi
@RestController
@RequestMapping("/base/public")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Client)
public class PublicController extends BaseApi {

    private final SysSwitchService switchService;
    private final SysConfigService configService;
    private final FileInfoService fileInfoService;
    private final CmsTextService cmsTextService;
    private final WechatTabbarService tabbarService;
    private final SysDeptService deptService;

    /**
     * 公开上传最大文件大小（5MB）
     */
    private static final long PUBLIC_UPLOAD_MAX_SIZE = 5 * 1024 * 1024;

    /**
     * 允许的图片内容类型前缀
     */
    private static final String IMAGE_CONTENT_TYPE_PREFIX = "image/";

    /**
     * 查询单个开关状态
     */
    @Operation(summary = "查询开关状态", description = "根据开关键查询开关是否开启")
    @GetMapping("/switch/{switchKey}")
    public ApiResult<Boolean> getSwitchStatus(
            @Parameter(description = "开关键") @PathVariable String switchKey) {
        boolean enabled = switchService.isEnabled(switchKey);
        return ApiResult.success(enabled);
    }

    /**
     * 批量查询开关状态
     */
    @Operation(summary = "批量查询开关状态", description = "批量查询多个开关的状态")
    @PostMapping("/switch/batch")
    public ApiResult<Map<String, Boolean>> batchGetSwitchStatus(
            @Parameter(description = "开关键列表") @RequestBody List<String> switchKeys) {
        Map<String, Boolean> result = switchService.batchGetStatus(switchKeys);
        return ApiResult.success(result);
    }

    // ==================== 配置相关 ====================

    /**
     * 获取配置值
     */
    @Operation(summary = "获取配置值", description = "根据配置键获取配置值")
    @GetMapping("/config/{configKey}")
    public ApiResult<String> getConfigValue(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        String value = configService.getValueByKey(configKey);
        return ApiResult.success(value);
    }

    // ==================== CMS文本相关 ====================

    /**
     * 根据文本标识获取文本内容
     * <p>
     * 此接口无需认证，用于获取用户协议、隐私政策等公开文本内容。
     * 仅返回已发布状态（status=1）的文本。
     * </p>
     *
     * @param textKey 文本标识（如: user_agreement, privacy_policy）
     * @return 文本内容
     */
    @Operation(summary = "获取公开文本内容", description = "根据文本标识获取公开的文本内容（用户协议、隐私政策等）")
    @GetMapping("/text/{textKey}")
    public ApiResult<CmsTextVO> getTextByKey(
            @Parameter(description = "文本标识", example = "user_agreement") @PathVariable String textKey) {
        CmsTextVO vo = cmsTextService.getByTextKey(textKey);
        if (vo == null) {
            return ApiResult.error(404, "文本内容不存在");
        }
        // 只返回已发布的文本
        if (vo.getStatus() == null || vo.getStatus() != 1) {
            return ApiResult.error(404, "文本内容暂未发布");
        }
        // 增加浏览次数
        cmsTextService.incrementViewCount(vo.getId());
        return ApiResult.success(vo);
    }

    // ==================== 微信小程序配置 ====================

    /**
     * 获取启用的底部菜单
     */
    @Operation(summary = "获取启用的底部菜单", description = "微信小程序底部菜单配置")
    @GetMapping("/wechat/tabbar/list")
    public ApiResult<List<WechatTabbarVO>> listWechatTabbar() {
        List<WechatTabbarVO> list = tabbarService.listEnabled();
        return ApiResult.success(list);
    }

    // ==================== 部门相关 ====================

    /**
     * 获取公开部门树
     * <p>
     * 此接口无需认证，用于 Demo 展示等场景。
     * </p>
     */
    @Operation(summary = "获取公开部门树", description = "无需认证的部门树接口，用于 Demo 展示")
    @GetMapping("/system/dept/tree")
    public ApiResult<List<SysDeptTreeVO>> getPublicDeptTree() {
        List<SysDeptTreeVO> tree = deptService.getDeptTree();
        return ApiResult.success(tree);
    }

    // ==================== 文件上传相关 ====================

    /**
     * 获取公开上传预签名URL（仅限头像等场景）
     * <p>
     * 此接口无需认证，用于注册时上传头像等场景。
     * 仅允许特定的业务类型（avatar, register_avatar）。
     * </p>
     */
    @Operation(summary = "获取公开上传预签名URL", description = "无需认证的文件上传预签名接口，仅限头像上传")
    @PostMapping("/upload/presign")
    public ApiResult<PresignedUploadResult> publicPresign(@Valid @RequestBody PresignRequestDTO dto) {
        // 检查业务类型是否允许公开上传
        String bizType = dto.getBizType();
        if (PublicUploadBizTypeEnum.of(bizType) == null) {
            return ApiResult.error(400, "不允许的上传类型，仅支持头像上传");
        }

        // 检查文件大小（限制5MB）
        if (dto.getFileSize() > PUBLIC_UPLOAD_MAX_SIZE) {
            return ApiResult.error(400, "文件大小不能超过5MB");
        }

        // 检查文件类型（仅限图片）
        String contentType = dto.getContentType();
        if (contentType == null || !contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX)) {
            return ApiResult.error(400, "仅支持图片格式");
        }

        // 生成预签名URL
        PresignedUploadResult result = fileInfoService.generatePresignedUrl(dto);
        return ApiResult.success(result);
    }

}
