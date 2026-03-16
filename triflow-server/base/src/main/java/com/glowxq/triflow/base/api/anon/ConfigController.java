package com.glowxq.triflow.base.api.anon;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.configuration.AppConfig;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.system.pojo.vo.ConfigVersionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AnonApi;

/**
 * 系统配置控制器
 * <p>
 * 提供系统配置相关的公开接口（无需登录）。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Tag(name = "系统配置", description = "系统配置相关接口")
@AnonApi
@RestController
@RequestMapping("/base/public/config")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class ConfigController extends BaseApi {

    private final AppConfig appConfig;

    /**
     * 获取配置版本号
     * <p>
     * 用于前端检查配置版本，决定是否清除本地缓存。
     * 此接口无需登录即可访问。
     * </p>
     */
    @Operation(summary = "获取配置版本号", description = "获取应用版本号和前端缓存版本号，无需登录")
    @GetMapping("/version")
    public ApiResult<ConfigVersionVO> getVersion() {
        ConfigVersionVO vo = new ConfigVersionVO(
            appConfig.getVersion(),
            appConfig.getPreferencesVersion()
        );
        return ApiResult.success(vo);
    }
}
