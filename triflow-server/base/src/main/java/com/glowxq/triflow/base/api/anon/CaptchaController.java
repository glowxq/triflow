package com.glowxq.triflow.base.api.anon;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.captcha.pojo.vo.CaptchaStatusVO;
import com.glowxq.triflow.base.captcha.pojo.vo.CaptchaVO;
import com.glowxq.triflow.base.captcha.service.CaptchaService;
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
 * 验证码控制器
 * <p>
 * 公开接口，无需登录即可访问
 * </p>
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Tag(name = "验证码管理", description = "图片验证码相关接口（公开）")
@AnonApi
@RestController
@RequestMapping("/base/public/captcha")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Client)
public class CaptchaController extends BaseApi {

    private final CaptchaService captchaService;

    /**
     * 获取验证码状态
     */
    @Operation(summary = "获取验证码状态", description = "查询是否启用图片验证码")
    @GetMapping("/status")
    public ApiResult<CaptchaStatusVO> getCaptchaStatus() {
        CaptchaStatusVO status = captchaService.getCaptchaStatus();
        return ApiResult.success(status);
    }

    /**
     * 获取图片验证码
     */
    @Operation(summary = "获取图片验证码", description = "生成并返回 Base64 编码的图片验证码")
    @GetMapping("/image")
    public ApiResult<CaptchaVO> getCaptchaImage() {
        CaptchaVO captcha = captchaService.generateCaptcha();
        return ApiResult.success(captcha);
    }
}
